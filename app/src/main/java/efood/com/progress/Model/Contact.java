package efood.com.progress.Model;

import android.net.Uri;

/**
 * Created by loc on 02/04/2016.
 */
public class Contact {

    protected long contactId;
    protected Uri contactUri;
    protected String displayName;
    protected String photoId;

     public  Contact(){

     }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public Uri getContactUri() {
        return contactUri;
    }

    public void setContactUri(Uri contactUri) {
        this.contactUri = contactUri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }
}
