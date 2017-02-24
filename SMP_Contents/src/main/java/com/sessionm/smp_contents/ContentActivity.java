package com.sessionm.smp_contents;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.SessionM;
import com.sessionm.api.content.ContentsManager;
import com.sessionm.api.content.data.Content;
import com.squareup.picasso.Picasso;

public class ContentActivity extends AppCompatActivity {

    TextView titleTextView;
    ImageView imageView;
    private ContentsManager _contentsManager = SessionM.getInstance().getContentsManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        titleTextView = (TextView) findViewById(R.id.content_title_textview);
        imageView = (ImageView) findViewById(R.id.content_main_imageview);
        String contentID = getIntent().getExtras().getString("content_id");

        if (contentID == null) {
            Toast.makeText(this, "No content found! ", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Content selectedContent = null;
            for (Content content : _contentsManager.getContents()) {
                if (contentID.equals(content.getID()))
                    selectedContent = content;
            }
            if (selectedContent != null)
                updateContent(selectedContent);
        }
    }

    private void updateContent(final Content content) {
        titleTextView.setText(content.getName());

        String imageURL = content.getImageURL();
        if (imageURL != null && !imageURL.equals("null")) {
            Picasso.with(this).load(imageURL).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle extras = new Bundle();
                    String url = "https://grafton.s3.amazonaws.com/content_demo/" + content.getExternalID();
                    //Hacking...in external id it's -mp4 instead of .mp3
                    url = url.replace("-", ".");
                    extras.putString("url", url);
                    Intent intent = new Intent(ContentActivity.this, VideoActivity.class);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });
        }
    }
}
