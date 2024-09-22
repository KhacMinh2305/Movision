package architecture.ui.view.custom;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.movision.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavBar extends BottomNavigationView {

    public BottomNavBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setItemActiveIndicatorColor(ColorStateList.valueOf(getResources().getColor(R.color.transparent, null)));
    }
}
