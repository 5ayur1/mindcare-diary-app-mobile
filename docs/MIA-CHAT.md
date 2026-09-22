# Chat com a MIA — texto e revisão

Na tela do diário, **Chat com a MIA** abre uma Activity Compose dedicada. O diário
tradicional permanece disponível. O botão **Segure para falar** controla o reconhecimento nativo dentro do Chat, com permissão `RECORD_AUDIO`. Não são gravados arquivos de áudio.

## Fluxo

1. Escreva e envie uma mensagem. O backend responde por `POST /mia/mensagens`.
2. Continue a conversa. O rascunho fica apenas no ViewModel, sobrevivendo à rotação.
3. Toque em **Revisar e salvar registro**.
4. O aplicativo reúne somente as falas do paciente, na ordem, separadas por linhas.
5. Revise/edite o texto e escolha o humor, se desejar. Não é inferido pela IA.
6. **Confirmar e salvar diário** chama `POST /mia/registros` com um UUID de requisição.
7. O registro aparece no histórico como **Chat com a MIA**. **Ler registro** revela o texto.

Uma nova tentativa de salvamento usa o mesmo UUID e o mesmo texto. Após uma falha
de rede, os campos de revisão ficam bloqueados para evitar modificar uma requisição
que pode já ter sido gravada. Consulte o histórico antes de iniciar outro registro
se sair nessa situação. Não há reenvio automático ao recriar a tela.

Uma falha da resposta da MIA não impede revisar e salvar o que o paciente escreveu.
Tentar a resposta novamente não duplica a fala no rascunho. Não é permitido revisar
enquanto há resposta em andamento ou descartar silenciosamente texto não enviado.

## Privacidade

Conversas e rascunhos não são gravados em DataStore, Bundle, arquivo, log ou banco
local. Ao sair sem salvar ou após encerramento do processo, o rascunho é perdido.
A saída pede confirmação; após salvar, o estado da conversa é limpo.
A Activity do chat usa `FLAG_SECURE` para restringir captura e prévia da tela.
As mensagens enviadas podem ser processadas pelo provedor de IA do backend; a tela
informa isso. Apenas o texto confirmado é persistido como diário pelo servidor.

O Retrofit/OkHttp e o JWT existentes são reutilizados. A identidade do paciente
vem da autenticação no servidor; o corpo do salvamento não contém paciente/usuário.
Cada mensagem tem limite de 4000 caracteres, e o registro final, 20000.
O backend ainda não mantém contexto entre chamadas da MIA: o histórico visual é local.

## Compatibilidade

`RegistroDiario` recebeu `id`, `textoConfirmado` e `origem` opcionais para ler o
histórico antigo. A resposta vazia do cadastro tradicional foi corrigida para
`Unit`, com feedback de carregamento, sucesso e erro. O humor inicial agora é um
valor válido (`SEM_DEFINICAO`). Logs de dados retornados foram removidos do
`PacienteViewModel` porque passam a carregar também o novo texto confirmado.

O histórico e o cartão do profissional exibem o texto confirmado, sem inventar
pontos positivos/negativos. O backend autoriza a leitura para o próprio paciente
ou seu profissional vinculado. A integração da geração por IA dos relatórios é
uma etapa posterior; esta entrega não altera o batch nem o dashboard Angular.

## Execução e testes

- Backend desta etapa em execução, com a migração aditiva aplicada ao PostgreSQL.
- Java 21, Android SDK 36 e Build Tools 35.0.0, conforme o projeto existente.
- O endereço existente `10.0.2.2:8080` atende ao emulador Android; ele não identifica
  o computador a partir de um telefone físico. Não foi alterada configuração de rede.

```text
gradlew.bat :app:assembleDebug :app:testDebugUnitTest
gradlew.bat :app:connectedDebugAndroidTest
```

Foi adicionada somente a dependência de teste `kotlinx-coroutines-test:1.8.1`.
O `testNamespace` foi separado do namespace do aplicativo, pois os valores iguais
impediam a compilação dos testes instrumentados no projeto existente.
`ChatViewModelTest` testa filtragem do rascunho, revisão, repetição idempotente,
falhas, limites e prevenção de envio duplicado. `ChatMiaScreenTest` contém testes
Compose de revisão/salvamento e confirmação de saída, para dispositivo/emulador.

Teste manual com dados fictícios: envie dois relatos, confira que as perguntas da
MIA não entram na revisão, edite o texto, salve e leia o histórico. Repita com a IA
indisponível e com falha de rede no salvamento. Confira também rotação, teclado,
voltar com rascunho, diário tradicional e leitura por profissional vinculado.

## Entrada por voz: segurar e soltar

Na primeira utilização, autorize o microfone e pressione o botão novamente. Segure **Segure para falar** durante o relato e solte para concluir. No emulador, mantenha pressionado o botão do mouse. O reconhecimento usa SpeechRecognizer, sem janela externa e sem arquivos de áudio. O serviço Android pode processar áudio na internet. A permissão de microfone agora pertence também ao MindCare.

O app solicita tolerância de silêncio de 10 segundos e formatação automática (Android 13+). O serviço pode ignorar essas opções. Se encerrar um trecho por silêncio, a escuta reinicia enquanto o botão estiver pressionado; os resultados são reunidos. Pode haver pequenos intervalos entre sessões: isto não equivale a gravação contínua garantida. Falhas de rede/permissão interrompem a captura com aviso. Ao soltar, aguardamos o resultado final por até cinco segundos; se houver timeout, preservamos o texto parcial com aviso para revisão. Rotação, saída da tela e ida ao segundo plano encerram a captura e recuperam os trechos disponíveis.

O resultado só preenche o campo. Envio e salvamento continuam explícitos. O limite total do campo é 4000 caracteres; ultrapassá-lo mantém o rascunho anterior e mostra aviso. Não se acrescentam pontos com regras que possam mudar o sentido. Para acessibilidade, a ação semântica do botão alterna iniciar/concluir o ditado.

Teste manual: segure, fale, pause, continue e solte; confira ordem dos trechos e ausência de envio automático. Teste soltar sem falar, negar permissão, sair da tela, rotação, rede indisponível e microfone do emulador. O serviço pode emitir sons de início/fim de sessão.

## Continuidade das perguntas

O aplicativo reconhece somente as perguntas normais previamente aprovadas do backend. A pergunta de abertura aparece uma vez. Respostas genéricas sobre o dia/acontecimentos dão lugar a “E como você se sentiu sobre isso?”. Perguntas já apresentadas dão lugar a outra pergunta neutra ainda não usada; ao esgotar as opções, a MIA oferece continuar escrevendo ou revisar o relato. Mensagens de segurança e fallback não são modificadas.

Esta política usa o histórico visual local e não envia conteúdo adicional à API. Ainda não constitui uma conversa com contexto completo no modelo. O histórico permanece apenas em memória.

Referências: https://developer.android.com/reference/android/speech/SpeechRecognizer e https://developer.android.com/reference/android/speech/RecognizerIntent
