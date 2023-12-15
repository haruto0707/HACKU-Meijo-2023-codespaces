import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.raywenderlich.android.sleepguardian.R

class Setting : Fragment() {

    private lateinit var audioManager: AudioManager
    private lateinit var volumeSeekBar: SeekBar
    private lateinit var volumeTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        volumeSeekBar = view.findViewById(R.id.volumeSeekBar)
        volumeTextView = view.findViewById(R.id.volumeText)

        // 音量の最大値をSeekBarの最大値に設定
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        volumeSeekBar.max = maxVolume

        // 現在の音量を取得し、SeekBarとTextViewに反映
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        volumeSeekBar.progress = currentVolume
        updateVolumeTextView(currentVolume, maxVolume)

        // SeekBarの変更をリスニング
        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // SeekBarの変更があった場合、音量を変更
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                    updateVolumeTextView(progress, maxVolume)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 何もしない
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 何もしない
            }
        })

        return view
    }

    private fun updateVolumeTextView(currentVolume: Int, maxVolume: Int) {
        // 音量のパーセンテージを計算し、TextViewに表示
        val volumePercentage = (currentVolume.toFloat() / maxVolume.toFloat() * 100).toInt()
        volumeTextView.text = getString(R.string.volume_percentage, volumePercentage)
    }
}
