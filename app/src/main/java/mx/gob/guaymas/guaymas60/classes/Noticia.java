package mx.gob.guaymas.guaymas60.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Arturo García Saiza on 11-feb-16.
 */
public class Noticia  implements Parcelable {

private int id;
    private  String url;
    private String status;
    private String title;
    private String content;
    private String date;
    private String categoria;
    private int  img;
    private String urlimg;
    public Noticia() {
    }

    public Noticia(int id, String url, String status, String date, String content, String title, String categoria, int img) {
        this.id = id;
        this.url = url;
        this.status = status;
        this.date = date;
        this.content = content;
        this.title = title;
        this.categoria = categoria;
        this.img = img;
    }


    public Noticia(int id, String url, String status, String date, String content, String title, String categoria, String urlimg) {
        this.id = id;
        this.url = url;
        this.status = status;
        this.date = date;
        this.content = content;
        this.title = title;
        this.categoria = categoria;
        this.urlimg = urlimg;
    }




    public Noticia(int id, String title, int img) {
        this.id=id;
        this.title = title;
        this.img = img;
    }

    public Noticia(int id, String title, String urlimg) {
        this.id=id;
        this.title = title;
        this.urlimg = urlimg;
    }


    protected Noticia(Parcel in) {
        id = in.readInt();
        url = in.readString();
        status = in.readString();
        title = in.readString();
        content = in.readString();
        date = in.readString();
        categoria = in.readString();
        img = in.readInt();
        urlimg = in.readString();
    }

    public static final Creator<Noticia> CREATOR = new Creator<Noticia>() {
        @Override
        public Noticia createFromParcel(Parcel in) {
            return new Noticia(in);
        }

        @Override
        public Noticia[] newArray(int size) {
            return new Noticia[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getUrlimg() {
        return urlimg;
    }

    public void setUrlimg(String urlimg) {
        this.urlimg = urlimg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(url);
        dest.writeString(status);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(date);
        dest.writeString(categoria);
        dest.writeInt(img);
        dest.writeString(urlimg);
    }



}
