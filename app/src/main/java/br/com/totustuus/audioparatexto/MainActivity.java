package br.com.totustuus.audioparatexto;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_GRAVAR = 200;
    private TextView campoTexto;
    private FloatingActionButton botaoGravarAudio;
    private boolean concatenacao;
    private String conteudoCampoTexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        campoTexto = findViewById(R.id.activity_main_texto);
        botaoGravarAudio = findViewById(R.id.activity_main_botao_gravar_audio);

        botaoGravarAudio.setOnClickListener((View v) -> isDeveRealizarConcatenacao());
    }

    private void isDeveRealizarConcatenacao() {

        concatenacao  = false;

        conteudoCampoTexto = campoTexto.getText().toString();

        if(!conteudoCampoTexto.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Realizar Concatenação?")
                    .setMessage("Deseja realizar uma concatenação com o conteúdo já existente no campo de texto?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        concatenacao = true;
                        gravar();
                    })
                    .setNegativeButton("Não", (dialog, which) -> gravar())
                    .show();
        } else {
            gravar();
        }
    }

    private void gravar() {

        // Intent para mostar o áudio em texto

        /*
         ACTION_RECOGNIZE_SPEECH = Inicia uma Activity que solicita a fala do usuário e a
         envia através de um reconhecedor de fala.
         Os resultados serão retornados por meio dos resultados da Acitivity (onActivityResult),
         ou encaminhados por meio de um PendingIntent, se houver.

         EXTRA_LANGUAGE_MODEL = informa ao reconhecedor qual modelo de fala prefere quando
         executar ACTION_RECOGNIZE_SPEECH. O reconhecedor usa essas informações para ajustar os
         resultados.
         Este extra é necessário. As atividades que implementam ACTION_RECOGNIZE_SPEECH podem
         interpretar os valores como entenderem.

         LANGUAGE_MODEL_FREE_FORM = Use a language model based on free-form speech recognition.

         EXTRA_LANGUAGE = Tag de idioma IETF opcional (conforme definido pelo BCP 47),
         por exemplo "en-US". Essa tag informa o reconhecedor a executar o reconhecimento de
         fala em um idioma diferente daquele definido em Locale.getDefault().


         EXTRA_PROMPT = Prompt de texto opcional para mostrar ao usuário ao solicitar que ele fale.
         */
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Diga alguma coisa...");

        try {
            // Iniciando Intent
            // Abre o Dialog
            startActivityForResult(intent, REQ_CODE_GRAVAR);
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    ;

    /*
    Recebe a entrada de voz e a manipula...
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_GRAVAR && resultCode == RESULT_OK && data != null) {

            // Pega um array de String da Intent
            ArrayList<String> mensagens = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if(concatenacao) {
                campoTexto.setText(conteudoCampoTexto + "\n" + mensagens.get(0) +".");
            } else {
                campoTexto.setText(mensagens.get(0) + ".");
            }

        } else {
            Toast.makeText(this, "Problema ao transcrever áudio...", Toast.LENGTH_LONG).show();
        }
    }
}
