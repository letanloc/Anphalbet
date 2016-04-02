package efood.com.progress.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import efood.com.progress.Async.AsyncTaskEx;
import efood.com.progress.Async.AsyncTaskThreadPool;
import efood.com.progress.Model.Contact;
import efood.com.progress.R;
import efood.com.progress.ad.CircularContactView;
import efood.com.progress.ad.ContactImageUtil;
import efood.com.progress.ad.ImageCache;
import efood.com.progress.unit.lbs.SearchablePinnedHeaderListViewAdapter;
import efood.com.progress.unit.lbs.StringArrayAlphabetIndexer;

/**
 * Created by loc on 02/04/2016.
 */
public class Adapter extends SearchablePinnedHeaderListViewAdapter<Contact> {
    private ArrayList<Contact> mContacts;
    private int CONTACT_PHOTO_IMAGE_SIZE;
    private int[] PHOTO_TEXT_BACKGROUND_COLORS;
    // mini mum max va  bo qua
    private final AsyncTaskThreadPool mAsyncTaskThreadPool = new AsyncTaskThreadPool(1, 2, 10);


    // thuc hien sắp xếp trình tự  của cách char
    @Override
    public CharSequence getSectionTitle(int sectionIndex) {
        /// cahcws
        // thuc hien lấy uget  tuc mản
        return ((StringArrayAlphabetIndexer.AlphaBetSection) getSections()[sectionIndex]).getName();
    }

    // thcu hien nhan gia tri vao
    public Adapter(Context context, final ArrayList<Contact> contacts) {
        setData(contacts);
        // tạo mot bien lay mau
        PHOTO_TEXT_BACKGROUND_COLORS = context.getResources().getIntArray(R.array.contacts_text_background_colors);
        // thuc hien lay  image size
        CONTACT_PHOTO_IMAGE_SIZE = context.getResources().getDimensionPixelSize(
                R.dimen.list_item__contact_imageview_size);
    }

    // thuc hine nhan data vao
    public void setData(final ArrayList<Contact> contacts) {
        this.mContacts = contacts;
        // tao mot mang  gê la ay toan bo ra theo contac
        final String[] generatedContactNames = generateContactNames(contacts);
        setSectionIndexer(new StringArrayAlphabetIndexer(generatedContactNames, true));
    }

    // day la mang gender
    private String[] generateContactNames(final List<Contact> contacts) {
        final ArrayList<String> contactNames = new ArrayList<String>();
        if (contacts != null)
            // thuch hien chay vong lap va lay tat ca cac du lieu ra
            for (final Contact contactEntity : contacts)
                contactNames.add(contactEntity.getDisplayName());
        return contactNames.toArray(new String[contactNames.size()]);
    }


    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final ViewHoler holder;
        View view;
        // thuc hien tao moi mot conn tact
        if (convertView == null) {
            holder = new ViewHoler();
            /// thuc hien goi
            // create new app
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item, parent, false);
            // thuc hien goi cac id
            holder.friendProfileCircularContactView = (CircularContactView) view.findViewById(R.id.listview_item__friendPhotoImageView);
            holder.headerView = (TextView) view.findViewById(R.id.header_text);
            holder.friendName = (TextView) view.findViewById(R.id.listview_item__friendNameTextView);
            view.setTag(holder);
        } else {
            view = convertView;
            // thuc hien lay du lieu
            holder = (ViewHoler) view.getTag();
        }
        // thv
        // call class Contact
        final Contact contact = getItem(position);
        /// call display name
        String displayname = contact.getDisplayName();
        //
        holder.friendName.setText(displayname);
        // nếu nhuư con tact get photo i
        boolean hasPhoto = !TextUtils.isEmpty(contact.getPhotoId());

        if (holder.updateTask != null && holder.updateTask.isCancelled())
            // chawcs day lafsu kien an cai heder
            holder.updateTask.cancel(true);
        // thuc hine taon image
        final Bitmap cachedBitmap = hasPhoto ? ImageCache.INSTANCE.getBitmapFromMemCache(contact.getPhotoId()) : null;

        if (cachedBitmap != null) {
            // neues nhu casi bitmap khong rong thif set nos vaof theo kieu img bitmap
            holder.friendProfileCircularContactView.setImageBitmap(cachedBitmap);
        }
        // su kien neu no rong thi thuc hine get  backgroud set color va lay cai chu
        else {

            final int backgroundColorToUse = PHOTO_TEXT_BACKGROUND_COLORS[position
                    % PHOTO_TEXT_BACKGROUND_COLORS.length];
            // nue nhu  gia tri cua display name  rong
            if (TextUtils.isEmpty(displayname)) // nues nhu
//                / thuc thuc hine set gia tri  icon cho cai icon dại dien cua list item
                holder.friendProfileCircularContactView.setImageResource(R.mipmap.ic_launcher,
                        backgroundColorToUse);
            else {
                // neu nhu n khon rong thi thuc hien cat chuoi vị trí đầu tiên cua app
                final String characterToShow = TextUtils.isEmpty(displayname) ? "" : displayname.substring(0, 1).toUpperCase(Locale.getDefault());
                //
                holder.friendProfileCircularContactView.setTextAndBackgroundColor(characterToShow, backgroundColorToUse);
            }
            // kiemr tra du lieu dung sai tuc cái photo id do  co hay ko neu co thi
            if (hasPhoto) {
                /// thuc hien lay du lieu dua tren cphto id tu contact
                holder.updateTask = new AsyncTaskEx<Void, Void, Bitmap>() {

                    @Override
                    public Bitmap doInBackground(final Void... params) {
                        if (isCancelled())
                            return null;
                        final Bitmap b = ContactImageUtil.loadContactPhotoThumbnail(parent.getContext(), contact.getPhotoId(), CONTACT_PHOTO_IMAGE_SIZE);
                        if (b != null)
                            return ThumbnailUtils.extractThumbnail(b, CONTACT_PHOTO_IMAGE_SIZE,
                                    CONTACT_PHOTO_IMAGE_SIZE);
                        return null;
                    }

                    @Override
                    public void onPostExecute(final Bitmap result) {
                        super.onPostExecute(result);
                        if (result == null)
                            return;
                        ImageCache.INSTANCE.addBitmapToCache(contact.getPhotoId(), result);
                        holder.friendProfileCircularContactView.setImageBitmap(result);
                    }
                };

            }
        }
        bindSectionHeader(holder.headerView, null, position);
        return view;
    }

    // thuch hien tao mot viewmoi
    class ViewHoler {
        // cái icn dai dien
        public CircularContactView friendProfileCircularContactView;
        // ten fren và cái tên kí tự hienr thi
        TextView friendName, headerView;
        public AsyncTaskEx<Void, Void, Bitmap> updateTask;
    }

    @Override
    public boolean doFilter(Contact item, CharSequence constraint) {
        // huc hie nkie tra

        if (TextUtils.isEmpty(constraint))
            return true;
        final String displayname = item.getDisplayName();

        // thuch hine viet hao hay thiung ì day
        return !TextUtils.isEmpty(displayname) && displayname.toLowerCase(Locale.getDefault())
                .contains(constraint.toString().toLowerCase(Locale.getDefault()));
    }

    @Override
    public ArrayList<Contact> getOriginalList() {
        return mContacts;
    }
//    @Override
//    public ArrayList<Contact> getOriginalList() {
//        return null;
//    }
}
