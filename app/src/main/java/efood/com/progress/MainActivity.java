package efood.com.progress;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import efood.com.progress.Adapter.Adapter;
import efood.com.progress.Model.Contact;
import efood.com.progress.ad.ContactsQuery;
import efood.com.progress.unit.lbs.PinnedHeaderListView;

public class MainActivity extends AppCompatActivity {

    private LayoutInflater mInflater;
    private PinnedHeaderListView mListView;
    private Adapter mAdapter;
    ArrayList<Contact> list;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = getContacts();
        // thuc hien xap x
        mInflater = LayoutInflater.from(MainActivity.this); 
        Collections.sort(list, new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                // day chac la noi tao ra tung nhom mot
                char lhsFirstLetter = TextUtils.isEmpty(lhs.getDisplayName()) ? ' ' : lhs.getDisplayName().charAt(0);
                char rhsFirstLetter = TextUtils.isEmpty(rhs.getDisplayName()) ? ' ' : rhs.getDisplayName().charAt(0);

                int firstLetterComparison = Character.toUpperCase(lhsFirstLetter) - Character.toUpperCase(rhsFirstLetter);
                if (firstLetterComparison == 0)
                    return lhs.getDisplayName().compareTo(rhs.getDisplayName());
                return firstLetterComparison;
            }
        });
// thuc hien tao moi mot listview
        mListView = (PinnedHeaderListView) findViewById(R.id.ls);
        mAdapter = new Adapter(this, list);

        int pinnedHeaderBackgroundColor = getResources().getColor(getResIdFromAttribute(this, android.R.attr.colorBackground));


        mAdapter.setPinnedHeaderBackgroundColor(pinnedHeaderBackgroundColor);
        mAdapter.setPinnedHeaderTextColor(getResources().getColor(R.color.pinned_header_text));
        mListView.setPinnedHeaderView(mInflater.inflate(R.layout.pinned_header_listview_side_header, mListView, false));
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(mAdapter);
        mListView.setEnableHeaderTransparencyChanges(false);
    }

    private boolean checkContactsReadPermission() {
        String permission = "android.permission.READ_CONTACTS";
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private ArrayList<Contact> getContacts() {

        if (checkContactsReadPermission()) {
            Uri uri = ContactsQuery.CONTENT_URI;
            final Cursor cursor = managedQuery(uri, ContactsQuery.PROJECTION, ContactsQuery.SELECTION, null, ContactsQuery.SORT_ORDER);
            if (cursor == null)
                return null;
            ArrayList<Contact> result = new ArrayList<>();
            while (cursor.moveToNext()) {
                Contact contact = new Contact();
                contact.setContactUri(ContactsContract.Contacts.getLookupUri(
                        cursor.getLong(ContactsQuery.ID),
                        cursor.getString(ContactsQuery.LOOKUP_KEY)));


                contact.setDisplayName(cursor.getString(ContactsQuery.DISPLAY_NAME));
                contact.setPhotoId(cursor.getString(ContactsQuery.PHOTO_THUMBNAIL_DATA));
                result.add(contact);
            }

            return result;
        }

        ArrayList<Contact> result = new ArrayList<>();
        //tạo ramdom
        Random r = new Random();
        // string buidert
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; ++i) {
            Contact contact = new Contact();
            /// sóa gì đây
            sb.delete(0, sb.length());
            // r nhan ga tri nhap vao la 1o va cong 1 ?

            int strLength = r.nextInt(10) + 1;
            // no
            for (int j = 0; j < strLength; ++j)
                switch (r.nextInt(3)) {
                    case 0:
                        // neus gía tri la ko
                        sb.append((char) ('a' + r.nextInt('z' - 'a')));
                        break;
                    case 1:
                        sb.append((char) ('A' + r.nextInt('Z' - 'A')));
                        break;
                    case 2:
                        sb.append((char) ('0' + r.nextInt('9' - '0')));
                        break;
                }

            contact.setDisplayName(sb.toString());
            result.add(contact);

        }
        return result;
    }


    public static int getResIdFromAttribute(final Activity activity, final int attr) {
        if (attr == 0)
            return 0;
        // loai du lieu
        final TypedValue typedValue = new TypedValue();
        // get them
        activity.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.resourceId;
    }
}
