package bangkit.roy.storyappmaster.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import bangkit.roy.storyappmaster.databinding.ItemStoryBinding
import bangkit.roy.storyappmaster.model.ListStoryItem
import bangkit.roy.storyappmaster.model.Story
import bangkit.roy.storyappmaster.ui.story.DetailStory
import com.bumptech.glide.Glide

class ListStoryAdapter:  PagingDataAdapter<ListStoryItem, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    class ListViewHolder(private val binding: ItemStoryBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(item: ListStoryItem) {
            Glide.with(binding.root.context)
                .load(item.photoUrl)
                .into(binding.imgPoster)
            binding.tvName.text = item.name
            binding.tvDeskripsi.text=item.description
            binding.tvDate.text=item.createdAt

            binding.root.setOnClickListener {
                val storyItem = Story(
                    item.name,
                    item.photoUrl,
                    item.createdAt,
                    item.description,
                    item.lat,
                    item.lon
                )

                val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    binding.root.context as Activity,
                    Pair(binding.imgPoster, "tv_imgPosterTrans"),
                    Pair(binding.tvName,"tv_nameTrans"),
                    Pair(binding.tvDeskripsi,"tv_deskripsiTrans"),
                    Pair(binding.tvDate,"tv_dateTrans")
                )

                val intentDetailStory = Intent(binding.root.context, DetailStory::class.java)
                intentDetailStory.putExtra(DetailStory.EXTRA_DETAIL_STORY, storyItem)
                binding.root.context.startActivity(intentDetailStory, optionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val bind = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(bind)
    }

    override fun onBindViewHolder(viewHolder: ListViewHolder, position: Int) {
        val item = getItem(position)
        if(item != null) {
            viewHolder.bind(item)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}