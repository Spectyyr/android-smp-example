package com.sessionm.smp_inbox;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.identity.IdentityManager;
import com.sessionm.api.identity.UserListener;
import com.sessionm.api.identity.UserManager;
import com.sessionm.api.identity.data.SMPUser;
import com.sessionm.api.inbox.InboxListener;
import com.sessionm.api.inbox.InboxManager;
import com.sessionm.api.inbox.data.InboxMessage;
import com.sessionm.api.inbox.data.NewInboxMessage;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import main.java.com.maximeroussy.invitrode.RandomWord;
import main.java.com.maximeroussy.invitrode.WordLengthException;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA5LTI3IDE1OjMwOjU1ICswMDAwIiwiZXhwIjoiMjAxNy0xMC0xMSAxNTozMDo1NSArMDAwMCIsImRhdGEiOnsiaWQiOiJkYTYxZGNkYS1hMzk4LTExZTctODcxZi05ZjZkNTQzYmUwNDAifX0.iBrHv9-INszE-SSL9rsuNnLDv7DBBaIUuqM6XDUvecxzap2CuoN4v3juXPvw-dZWuzbiHY2H3TPJJlRcI5_fZPxH2FjDqGA1S5nwEwEYVn9D1oMvnXUB6jLIq3ev4omE7ZUj5zVytsn_rKdryllfHro_8g5TneiOUoFBa_1N_RcC9AK_8640xbYPtZaNWhxsJiCwTsKWaLSYQ6RQv_xo1M4reL56dbjJ16Y-50HUy6Pxax6biKVvpjNRDizrkY0bka07lHMLAHMZD5-D3OYnxpxyg9aVX2kJd36iZuwsKaXVMtrCzwmzzGuhQD1PUUhC43wkNUbYw9z2d94v0FDxvQ";

    private Activity mContext;
    private MenuAdapter mMenuAdapter;
    private List<InboxMessage> messages;
    private SwipeMenuRecyclerView mSwipeMenuRecyclerView;
    private TextView userBalanceTextView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar actionBar = (Toolbar) findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);

        userBalanceTextView = (TextView) findViewById(R.id.user_balance_textview);
        userBalanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserManager.getInstance().getCurrentUser() == null)
                    IdentityManager.getInstance().authenticateCoalitionWithToken(SAMPLE_USER_TOKEN);
                else
                    IdentityManager.getInstance().logOutUser();
            }
        });
        mContext = this;

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        messages = new ArrayList<>();
        mSwipeMenuRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        mSwipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSwipeMenuRecyclerView.setHasFixedSize(true);
        mSwipeMenuRecyclerView.addItemDecoration(new ListViewDecoration());

        mSwipeMenuRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mSwipeMenuRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);

        mMenuAdapter = new MenuAdapter(messages);
        mMenuAdapter.setOnItemClickListener(onItemClickListener);
        mSwipeMenuRecyclerView.setAdapter(mMenuAdapter);

        FloatingActionButton createNewMessageButton = (FloatingActionButton) findViewById(R.id.create_new_message_button);
        createNewMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Optional: Random words for a new Inbox message
                String subject = "Sample Subject";
                String body = "Sample Body";
                Random random = new Random();
                int length = random.nextInt(6) + 3;
                try {
                    subject = RandomWord.getNewWord(length);
                    body = RandomWord.getNewWord(length) + " "
                            + RandomWord.getNewWord(length) + " "
                            + RandomWord.getNewWord(length) + " "
                            + RandomWord.getNewWord(length) + " "
                            + RandomWord.getNewWord(length) + " "
                            + RandomWord.getNewWord(length) + "Aenean scelerisque venenatis nisl vel imperdiet. In volutpat id ipsum et convallis. Duis suscipit, enim non auctor varius, nunc lectus ornare felis, quis dapibus mi ex sit amet ipsum. Mauris maximus ultricies odio, quis sodales orci facilisis vel. Sed vitae dui sit amet sapien posuere viverra. Nulla eget ex dui. Proin iaculis porttitor ullamcorper. Donec facilisis vitae ante sed viverra. Aenean nec libero varius, bibendum arcu in, ultricies mi. In et magna nec urna ornare tempus at vitae dui. Curabitur tincidunt placerat dolor at scelerisque. Nulla porttitor erat a risus faucibus vulputate. Aliquam vitae neque a arcu lacinia ullamcorper. Aliquam quis tincidunt tortor. Aliquam nec augue nisi. Praesent id sollicitudin tellus.";
                } catch (WordLengthException e) {
                    e.printStackTrace();
                }

                NewInboxMessage newInboxMessage = new NewInboxMessage(subject, body.toLowerCase());
                InboxManager.getInstance().createInboxMessage(newInboxMessage);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        InboxManager.getInstance().setListener(inboxListener);
        UserManager.getInstance().setListener(_userListener);
    }

    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.item_height);

            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(mContext)
                        .setBackgroundDrawable(R.drawable.selector_red)
//                        .setImage(R.mipmap.ic_action_delete)
                        .setText("Delete")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);

                SwipeMenuItem closeItem = new SwipeMenuItem(mContext)
                        .setBackgroundDrawable(R.drawable.selector_blue)
                        .setText("Mark As Read")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(closeItem);

                SwipeMenuItem addItem = new SwipeMenuItem(mContext)
                        .setBackgroundDrawable(R.drawable.selector_green)
                        .setText("Mark As Unread")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(addItem);
            }
        }
    };

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            SessionM.getInstance().getInboxManager().updateInboxMessageState(messages.get(position), InboxMessage.STATE_TYPES.READ);
            Intent intent = new Intent(MainActivity.this, InboxDetailActivity.class);
            intent.putExtra("index", position);
            startActivity(intent);
        }
    };

    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();

            InboxMessage message = messages.get(adapterPosition);
            if (menuPosition == 0) {
                SessionM.getInstance().getInboxManager().updateInboxMessageState(message, InboxMessage.STATE_TYPES.DELETED);
                messages.remove(adapterPosition);
                mMenuAdapter.notifyItemRemoved(adapterPosition);
            }
            if (menuPosition == 1) {
                SessionM.getInstance().getInboxManager().updateInboxMessageState(message, InboxMessage.STATE_TYPES.READ);
            }
            if (menuPosition == 2) {
                SessionM.getInstance().getInboxManager().updateInboxMessageState(message, InboxMessage.STATE_TYPES.NEW);
            }
        }
    };

    InboxListener inboxListener = new InboxListener() {
        @Override
        public void onInboxMessagesFetched(List<InboxMessage> list) {
            if (messages == null) {
                messages = new ArrayList<>();
            } else {
                messages.clear();
            }
            messages.addAll(list);
            mMenuAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onInboxMessagesStateUpdated(List<InboxMessage> list) {
            mMenuAdapter.notifyDataSetChanged();
        }

        @Override
        public void onInboxMessageCreated(InboxMessage inboxMessage) {
            Toast.makeText(MainActivity.this, "New message created! \n ID: " + inboxMessage.getID(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(SessionMError sessionMError) {
            swipeRefreshLayout.setRefreshing(false);
            Log.d("InboxDemo", sessionMError.getMessage());
        }
    };

    UserListener _userListener = new UserListener() {
        @Override
        public void onUserUpdated(SMPUser smpUser, Set<String> set) {
            if (smpUser != null) {
                userBalanceTextView.setText(smpUser.getAvailablePoints() + "pts");
            } else
                userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
            InboxManager.getInstance().fetchInboxMessages();
        }

        @Override
        public void onFailure(SessionMError sessionMError) {

        }
    };

    @Override
    public void onRefresh() {
        SessionM.getInstance().getInboxManager().fetchInboxMessages();
    }
}
