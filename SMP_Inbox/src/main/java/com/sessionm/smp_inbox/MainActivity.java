package com.sessionm.smp_inbox;

import android.app.Activity;
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

import com.sessionm.api.AchievementData;
import com.sessionm.api.SessionListener;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.User;
import com.sessionm.api.inbox.InboxListener;
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

import main.java.com.maximeroussy.invitrode.RandomWord;
import main.java.com.maximeroussy.invitrode.WordLengthException;

public class MainActivity extends AppCompatActivity implements SessionListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String SAMPLE_USER_TOKEN = "v2--Sd2T8UBqlCGQovVPnsUs4eqwFe0-1i9JV4nq__RWmsA=--dWM8r8RggUJCToOaiiT6NXmiOipkovvD9HueM_jZECStExtGFkZzVmCUhkdDJe5NQw==";

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
        final SessionM sessionM = SessionM.getInstance();
        userBalanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sessionM.getUser().isRegistered())
                    sessionM.authenticateWithToken("auth_token", SAMPLE_USER_TOKEN);
                else
                    sessionM.logOutUser();
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
                            + RandomWord.getNewWord(length);
                } catch (WordLengthException e) {
                    e.printStackTrace();
                }

                NewInboxMessage newInboxMessage = new NewInboxMessage(subject, body.toLowerCase());
                SessionM.getInstance().getInboxManager().createInboxMessage(newInboxMessage);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        SessionM.getInstance().getInboxManager().setListener(inboxListener);
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

    @Override
    public void onSessionStateChanged(SessionM sessionM, SessionM.State state) {
        if (state == SessionM.State.STARTED_ONLINE)
            sessionM.authenticateWithToken("auth_token", SAMPLE_USER_TOKEN);
    }

    @Override
    public void onSessionFailed(SessionM sessionM, int i) {

    }

    @Override
    public void onUserUpdated(SessionM sessionM, User user) {
        if (user.isRegistered())
            userBalanceTextView.setText(user.getPointBalance() + "pts");
        else
            userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
        sessionM.getInboxManager().fetchInboxMessages();
    }

    @Override
    public void onUnclaimedAchievement(SessionM sessionM, AchievementData achievementData) {

    }

    @Override
    public void onRefresh() {
        SessionM.getInstance().getInboxManager().fetchInboxMessages();
    }
}
