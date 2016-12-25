package guide.by.android.com.guide.model;

/**
 * Created by by.huang on 2016/11/17.
 */

public class LanguageModel {

    private String language;
    private boolean isSelect;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public static LanguageModel getModel(String language) {
        LanguageModel model = new LanguageModel();
        model.language = language;
        model.isSelect = false;
        return model;
    }
}
