package in.tts.model;

import android.graphics.Bitmap;

import com.google.firebase.perf.metrics.AddTrace;

public class ImageItem {
    private Bitmap image;
    private String title;

    @AddTrace(name = "onCallImageItem", enabled = true)
    public ImageItem(Bitmap image, String title) {
        super();
        this.image = image;
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
