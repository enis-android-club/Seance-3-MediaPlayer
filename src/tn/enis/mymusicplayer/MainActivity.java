package tn.enis.mymusicplayer;

import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

  private MediaPlayer mediaPlayer;
  private ImageButton playBtn, pauseBtn;
  private TextView    textView;
  private SeekBar     seekBar;
  
  private Handler  handler = new Handler();
  private Runnable UpdateSongTime;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    // Cr�er une instance de MediaPlayer
    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.song);
    
    // Activer la r�petition
    mediaPlayer.setLooping(true);

    // Charger mes composantes de l'interface
    textView = (TextView) findViewById(R.id.elapsedTime);
    playBtn  = (ImageButton) findViewById(R.id.play);
    pauseBtn = (ImageButton) findViewById(R.id.pause);
    
    // D�sactiver le bouton Pause (On n'a pas de 'pause' si on ne fait pas 'play')
    pauseBtn.setEnabled(false);

    seekBar = (SeekBar) findViewById(R.id.seekBar);
    
    // Affecter la longueur par d�faut de notre seekBar
    seekBar.setMax(mediaPlayer.getDuration());
    
    // Cr�er un listener (r�cepteur des �venements) pour le seek bar
    // pour r�cuperer le changement du valeur par l'utilisateur
    seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
      
      // Ces 2 m�thodes sont obligatoires � mettre, mais on ne les utilise pas dans notre cas
      public void onStopTrackingTouch(SeekBar seekBar) {}
      public void onStartTrackingTouch(SeekBar seekBar) {}
      
      // C'est la m�thode qu'on a besoin
      // Le 1er param�tre contient le seekBar cliqu�
      // Le 2�me contient la valeur finale s�l�ctionn�e
      // Le 3�me indique si cette valeur est s�l�ctionn�e
      // par l'utilisateur ou bien par le programme
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // Si l'utilisateur change la valeur
        if (fromUser) {
          // On cherche le nouvel emplacement dans le mediaPlayer
          mediaPlayer.seekTo(progress);
        }
      }
    });

  }

  // Une m�thode pour convertir le temps en millisecondes en format mm:ss
  public String millisecondFormat(int ms) {
    // On calcule le nombre des minutes
    long minutes = TimeUnit.MILLISECONDS.toMinutes((long) ms);
    // Puis le nombre des secondes restantes
    long seconds = TimeUnit.MILLISECONDS.toSeconds((long) ms) - TimeUnit.MINUTES.toSeconds(minutes);
    
    // puis on les rassemble dans une chaine de caract�res
    // %02d => { %d signifie entier, %02d signifie un entier de longeur 2}
    // 1 avec %d   sera  "1"
    // 1 avec %02d sera "01"
    String formattedTime = String.format("%02d:%02d", minutes, seconds);

    return formattedTime;
  }

  // L'action du bouton Play
  public void playAction(View v) {
    // D�marrer le mediaPlayer
    mediaPlayer.start();
    
    // Activer le btn pause et d�sactiver le btn play
    pauseBtn.setEnabled(true);
    playBtn.setEnabled(false);

    // Cr�er un objet de type Runnable (contenant un m�thode 'public void run(){}')
    // Cet objet sert � ex�cuter le code du m�thode run() en arri�re plan
    // 
    UpdateSongTime = new Runnable() {
      public void run() {
        // Changer le niveau actuel du seekBar
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        
        // Mettre � jour le texte
        textView.setText(
          millisecondFormat(mediaPlayer.getCurrentPosition()) +
          " / " +
          millisecondFormat(mediaPlayer.getDuration())
        );
        
        // Re-ex�cuter le m�me Runnable (UpdateSongTime) une autre fois apr�s 500ms
        handler.postDelayed(this, 500);
      }
    };
    
    // Ex�cuter le Runnable UpdateSongTime apr�s 500ms
    handler.postDelayed(UpdateSongTime, 500);
  }

  // L'action du bouton Pause
  public void pauseAction(View v) {
    // Mettre en pause le lecteur
    mediaPlayer.pause();

    // Activer le bouton Play et d�sactiver le bouton Pause
    playBtn.setEnabled(true);
    pauseBtn.setEnabled(false);
  }

  // La m�thode � ex�cuter quand on quite l'application
  @Override
  protected void onStop() {
    super.onStop();
    // On arr�te les appels en cours au Runnable UpdateSongTime
    handler.removeCallbacks(UpdateSongTime);
    // On d�charge le lecteur du m�moire
    mediaPlayer.release();
  }

}
