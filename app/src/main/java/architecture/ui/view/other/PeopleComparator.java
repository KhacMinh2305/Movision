package architecture.ui.view.other;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import architecture.data.local.entity.People;

public class PeopleComparator extends DiffUtil.ItemCallback<People>{

    @Override
    public boolean areItemsTheSame(@NonNull People oldItem, @NonNull People newItem) {
        return oldItem.id == newItem.id;
    }

    @SuppressLint("DiffUtilEquals")
    @Override
    public boolean areContentsTheSame(@NonNull People oldItem, @NonNull People newItem) {
        return newItem.equals(oldItem);
    }
}
