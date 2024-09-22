package architecture.ui.view.other;

import architecture.data.model.genre.Genre;

public interface ChoseGenresCallback {
    void chose(Genre genre);
    void unChose(Genre genre);
}
