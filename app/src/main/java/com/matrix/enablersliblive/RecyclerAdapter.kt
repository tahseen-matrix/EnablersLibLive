package com.matrix.enablersliblive

/**
 * Created By Matrix Marketers
 */
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.matrix.enablersliblive.databinding.RowHorizontalBinding

class RecyclerAdapter(
    private val arrayList: MutableList<Int>, private val mContext: Context
) : RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            RowHorizontalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(arrayList[position])
        holder.itemBinding.root.setOnClickListener {
            mContext.startActivity(Intent(mContext,ColorActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class MyViewHolder(
         val itemBinding: RowHorizontalBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Int) {
            itemBinding.ivImage.setImageResource(data)
        }
    }
}