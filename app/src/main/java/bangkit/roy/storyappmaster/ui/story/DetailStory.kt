package bangkit.roy.storyappmaster.ui.story

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import bangkit.roy.storyappmaster.databinding.ActivityDetailStoryBinding
import bangkit.roy.storyappmaster.model.Story
import com.bumptech.glide.Glide

class DetailStory : AppCompatActivity() {



    private lateinit var detailStoryBinding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailStoryBinding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(detailStoryBinding.root)

        val detailCerita = intent.getParcelableExtra<Story>(EXTRA_DETAIL_STORY) as Story
        Glide.with(this)
            .load(detailCerita.photo)
            .into(detailStoryBinding.imgPosterDet)
        detailStoryBinding.tvNameDet.text = detailCerita.name
        detailStoryBinding.tvDeskripsiDet.text = detailCerita.description
        detailStoryBinding.tvDateDet.text= detailCerita.date
    }

    companion object {
        const val EXTRA_DETAIL_STORY = "Page Detail Story"
    }

}