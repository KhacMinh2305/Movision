package architecture.ui.view.other;
import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import architecture.data.model.people.PeopleItem;

public class SearchingItemPeopleComparator extends DiffUtil.ItemCallback<PeopleItem> {
    @Override
    public boolean areItemsTheSame(@NonNull PeopleItem oldItem, @NonNull PeopleItem newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @SuppressLint("DiffUtilEquals")
    @Override
    public boolean areContentsTheSame(@NonNull PeopleItem oldItem, @NonNull PeopleItem newItem) {
        return oldItem.equals(newItem);
    }
}
