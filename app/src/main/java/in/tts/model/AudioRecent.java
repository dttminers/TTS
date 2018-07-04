package in.tts.model;

public class AudioRecent {

    private String Filename, FileSize;

    public AudioRecent(String Filename, String FileSize) {
        this.Filename = Filename;
        this.FileSize = FileSize;
    }

    public String getFilename() {
        return Filename;
    }

    public void setFilename(String Filename) {
        this.Filename = Filename;
    }

    public String getFileSize() {
        return FileSize;
    }

    public void setFileSize(String FileSize) {
        this.FileSize = FileSize;
    }
}
