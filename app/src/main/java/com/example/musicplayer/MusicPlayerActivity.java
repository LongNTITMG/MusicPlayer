package com.example.musicplayer;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musicplayer.Song.model.Song;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class MusicPlayerActivity extends AppCompatActivity {

    private ImageView coverImageView;
    private TextView titleTextView;
    private TextView artistTextView;
    private TextView total_Time;
    private TextView start_Time;

    private SeekBar seekBar;
    private ImageView prevButton;
    private ImageView playPauseButton;
    private ImageView nextButton;
    private ImageView loopButton;
    private ImageView shuffleButton;

    private MediaPlayer mediaPlayer;
    private Handler seekBarHandler;
    private List<Song> songList;
    //-----------nút ngau nhien
    private boolean shuffleMode = false;
    private boolean loopMode = false;

    private static final int STATE_STOPPED = 0;
    private static final int STATE_PLAYING = 1;
    private static final int STATE_PAUSED = 2;

    private int playerState = STATE_STOPPED;

    private Object lock = new Object();

    // luu tru dung de tim kiem
    String songID;
    String songTitle;
    String artistName;
    String imageUrl;
    String roundImageUrl;
    String songUrl;
    //------------animation----------------
    Animation animation;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        Anhxa();
        //-------animation
      //  animation = AnimationUtils.loadAnimation(this, R.anim.disc_rotate);
        //-----------------
        prepareMediaPlayer();
        // Retrieve song data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            songID = intent.getStringExtra("SONG_ID");
//            currentSongId = songId;
            songTitle = intent.getStringExtra("SONG_TITLE");
            artistName = intent.getStringExtra("ARTIST_NAME");
            imageUrl = intent.getStringExtra("IMAGE_URL");
            roundImageUrl = intent.getStringExtra("ROUND_IMAGE_URL");
            songUrl = intent.getStringExtra("SONG_URL");
            songList = (List<Song>) getIntent().getSerializableExtra("SONG_LIST");
//            Toast.makeText(MusicPlayerActivity.this, "Length of songList: " + songList.size(), Toast.LENGTH_SHORT).show();
            updateUI();
            // Initialize MediaPlayer
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(songUrl);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    // Start playing when MediaPlayer is prepared
//                    mp.start();
//                    updateTimeSong();
//                    updateSeekBar();
//                    playerState = STATE_PLAYING;
//                }
//            });
            mediaPlayer.setOnPreparedListener(mp -> {
                // Start playing when MediaPlayer is prepared
                mp.start();
                updateTimeSong();
                updateSeekBar();
                playerState = STATE_PLAYING;
            });

//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    // Start playing when MediaPlayer is prepared
//                    mp.start();
//                    updateTimeSong();
//                    updateSeekBar();
//                    playerState = STATE_PLAYING;
//                }
//            });
            playPauseButton.setOnClickListener(v -> {
                if (mediaPlayer != null) {
                    if (playerState == STATE_PLAYING) {
                        pauseMusic();

                    } else {
                        playMusic();

                    }
                }
            });
        } else {
            // Handle case when no song data is available
            finish();
        }

        //        Toast.makeText(MusicPlayerActivity.this, "currentSongID oncreate: " + currentSongId, Toast.LENGTH_SHORT).show();

        nextButton.setOnClickListener(e -> playNextSong());
        prevButton.setOnClickListener(e -> playPreSong());


        //-----xu ly nut ngau nhien---
        shuffleButton.setOnClickListener(e -> toggleShuffleMode());

        //---xu ly nut lap lai---
        loopButton.setOnClickListener(e -> toggleLoopMode());

        //----su kien nguoi dung tuong tac voi seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });

        //---------------------------------------------

    }
    //---------ham xu ly lap lai--------
    private void toggleLoopMode() {
        loopMode = !loopMode;
        if (loopMode) {
            Toast.makeText(this, "Loop Mode Enabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Loop Mode Disabled", Toast.LENGTH_SHORT).show();
        }
    }
    //---------ham xu ly nut ngau nhien
    private void toggleShuffleMode() {
        shuffleMode = !shuffleMode;
        // Update the UI to reflect the shuffle mode status (you can add an icon change or other visual indication)

        if (shuffleMode) {
            // Shuffle mode is enabled, update UI accordingly
            // You may also want to update the logic for the "Next" button when shuffle mode is enabled
            Toast.makeText(this, "Activate", Toast.LENGTH_SHORT).show();
            nextButton.setEnabled(false);
        } else {
            // Shuffle mode is disabled, update UI accordingly
            // Revert the logic for the "Next" button when shuffle mode is disabled
            Toast.makeText(this, "Deativate", Toast.LENGTH_SHORT).show();
            nextButton.setEnabled(true);
        }
    }
    private int getRandomPosition() {
        // Generate a random position within the songList size
        return (int) (Math.random() * songList.size());
    }

    // -----------------------------------------------

    // Thêm vào class MusicPlayerActivity
    private void playNextSong() {
        if (songList != null && songList.size() > 0 && mediaPlayer != null) {
            int currentPosition = getCurrentSongPosition();
            int nextPosition;
            if (shuffleMode) {
                nextPosition = getRandomPosition();
            } else {
                nextPosition = (currentPosition + 1);
                if (currentPosition == songList.size() - 1) {
                    nextPosition = 0;
                }
            }
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            // Lấy thông tin của bài hát kế tiếp
            Song nextSong = songList.get(nextPosition);

            //Toast.makeText(MusicPlayerActivity.this, "Next Song ID: " + nextSong.getSongID(), Toast.LENGTH_SHORT).show();

            // Phát bài hát kế tiếp
            playSong(nextSong);

            updateUI(nextPosition);

            // update current
            songID = nextSong.getSongID();
//            Toast.makeText(MusicPlayerActivity.this, "currentSongID" + currentSongId, Toast.LENGTH_SHORT).show();
            playPauseButton.setImageResource(R.drawable.pause);
        }
    }

    //--------------------------------------
    // xu ly prev song
    private void playPreSong() {
        if (songList != null && songList.size() > 0 && mediaPlayer != null) {
            int currentPosition = getCurrentSongPosition();
            int prePosition = (currentPosition - 1);
            if (currentPosition == 0) {
                prePosition = songList.size() - 1;
            }
            // Lấy thông tin của bài hát trước
            Song preSong = songList.get(prePosition);
            // Phát bài hát trước
            playSong(preSong);
            updateUI(prePosition);
            // update current
            songID = preSong.getSongID();
//            Toast.makeText(MusicPlayerActivity.this, "currentSongID" + currentSongId, Toast.LENGTH_SHORT).show();
            playPauseButton.setImageResource(R.drawable.pause);
        }

    }
    // tra ve vi tri hien tai cua bai hat
    private int getCurrentSongPosition() {
        if (songList != null && songList.size() > 0 && mediaPlayer != null) {
            for (int i = 0; i < songList.size(); i++) {
                if (songList.get(i).getSongID().equals(songID)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void playSong(Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();

            try {
                mediaPlayer.setDataSource(song.getSongUrl());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    //--------------------------------------------

    private void prepareMediaPlayer() {

    }

    private void Anhxa() {
        // Initialize view(ánh xạ)
        coverImageView = findViewById(R.id.coverImageView);
        titleTextView = findViewById(R.id.titleTextView);
        artistTextView = findViewById(R.id.artistTextView);
        total_Time = findViewById(R.id.txt_totalTime);
        start_Time = findViewById(R.id.txt_timeStart);
        seekBar = findViewById(R.id.seekBar);
        prevButton = findViewById(R.id.prevButton);
        playPauseButton = findViewById(R.id.playPauseButton);
        nextButton = findViewById(R.id.nextButton);
        loopButton = findViewById(R.id.loopButton);
        shuffleButton = findViewById(R.id.shuffleButton);
    }

    //----------lien quan xu ly seekbar-------------------
    //Cap nhat thanh tiem song
    private void updateTimeSong() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    if (mediaPlayer != null) {
                        SimpleDateFormat dinhDangGio = new SimpleDateFormat("mm:ss");
                        start_Time.setText(dinhDangGio.format(mediaPlayer.getCurrentPosition()));

                        //update progress seekbar
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }

                // kiem tra thoi gian ket thuc bai hat->next.
                if (mediaPlayer != null) {
                    if(loopMode){
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                playSong(songList.get(getCurrentSongPosition()));
                            }
                        });
                    }
                    else {
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                playNextSong();
                            }
                        });

                        }

                }
                handler.postDelayed(this, 100);
            }
        }, 100);
    }

    //-----------------------------------------------
//    Phương thức này cập nhật thanh seekbar để hiển thị thời gian tổng của bài hát và thiết lập giá trị tối đa cho seekbar bằng độ dài của bài hát.
//    total_Time là một TextView hiển thị thời gian tổng của bài hát.
//    seekBar là một thanh seekbar để người dùng có thể kéo và di chuyển để chọn vị trí trong bài hát.
    private void updateSeekBar() {
        //
        SimpleDateFormat dinhDangGio = new SimpleDateFormat("mm:ss");
        total_Time.setText(dinhDangGio.format(mediaPlayer.getDuration()));
        // gan max tg cua bai hat = mediaplayer.getduration
        seekBar.setMax(mediaPlayer.getDuration());
    }

//    Phương thức này được sử dụng để cập nhật giao diện người dùng với thông tin về bài hát.
//    Nó hiển thị tiêu đề bài hát, tên nghệ sĩ và hình ảnh của bài hát trên giao diện người dùng.
//    Trong trường hợp này, hình ảnh của bài hát được quay để tạo hiệu ứng quay như một đĩa nhạc.
    void updateUI() {
        // Update UI with song information
        titleTextView.setText(songTitle);
        artistTextView.setText(artistName);
        Glide.with(this).load(roundImageUrl).into(coverImageView);
        coverImageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.disc_rotate));
    }

//    Phương thức này cũng cập nhật giao diện người dùng với thông tin về bài hát, tuy nhiên, nó nhận một tham số là vị trí của bài hát trong danh sách và hiển thị thông tin của bài hát tại vị trí đó.
//    Các thông tin bài hát bao gồm tiêu đề bài hát, tên nghệ sĩ và hình ảnh bài hát.
    private void updateUI(int i) {
        titleTextView.setText(songList.get(i).getSongTitle());
        artistTextView.setText(songList.get(i).getArtistName());
        Glide.with(MusicPlayerActivity.this).load(songList.get(i).getRoundImageUrl()).into(coverImageView);
        coverImageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.disc_rotate));
    }

    //    Phương thức này bắt đầu phát nhạc nếu MediaPlayer đã được khởi tạo và không đang phát.
//    Nó cũng cập nhật trạng thái của trình phát nhạc và biểu tượng của nút play/pause.
    private void playMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            playerState = STATE_PLAYING;
            updateSeekBar();
            playPauseButton.setImageResource(R.drawable.pause);
        }
    }

//    Phương thức này dừng phát nhạc nếu MediaPlayer đang phát.
//    Nó cũng cập nhật trạng thái của trình phát nhạc và biểu tượng của nút play/pause.
    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playerState = STATE_PAUSED;
            playPauseButton.setImageResource(R.drawable.play);
        }
    }

//    Phương thức này giải phóng tài nguyên của MediaPlayer khi không cần thiết, đảm bảo rằng không còn bộ nhớ nào bị rò rỉ khi Activity bị hủy.
//    Nó được gọi trong phương thức onDestroy() của Activity để đảm bảo rằng MediaPlayer được giải phóng khi Activity kết thúc.
    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }
}