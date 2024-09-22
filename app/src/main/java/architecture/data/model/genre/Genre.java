package architecture.data.model.genre;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Genre {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("name")
    @Expose
    private String name;

    public Genre(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Genre clone() {
        return new Genre(id, name);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof Genre other)) {
            return false;
        }
        return Objects.equals(id, other.getId()) && name.equals(other.getName());
    }
}
