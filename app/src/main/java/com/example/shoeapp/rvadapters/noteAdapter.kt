import android.app.LauncherActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoeapp.Note
import com.example.shoeapp.R

class noteAdapter(private var items: List<Note>) : RecyclerView.Adapter<noteAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleItem: TextView = itemView.findViewById(R.id.titleNote)
        val descItem: TextView = itemView.findViewById(R.id.DescNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notelist_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.titleItem.text = item.title
        holder.descItem.text = item.content
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(newNotes: List<Note>) {
        items = newNotes
        notifyDataSetChanged()
    }
}
