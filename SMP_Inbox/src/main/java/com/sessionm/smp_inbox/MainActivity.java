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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.core.api.SessionM;
import com.sessionm.core.api.SessionMError;
import com.sessionm.identity.api.UserManager;
import com.sessionm.identity.api.data.SMPUser;
import com.sessionm.identity.api.provider.SessionMOauthProvider;
import com.sessionm.inbox.api.InboxManager;
import com.sessionm.inbox.api.data.InboxMessage;
import com.sessionm.inbox.api.data.NewInboxMessage;
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
    private static final String SAMPLE_USER_EMAIL = "sampleuser@sessionm.com";
    private static final String SAMPLE_USER_PWD = "sessionm1";
    private Activity _context;
    private MenuAdapter _menuAdapter;
    private List<InboxMessage> messages;
    private SwipeMenuRecyclerView _swipeMenuRecyclerView;
    private TextView userBalanceTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SessionMOauthProvider _sessionMOauthProvider;
    private UserManager _userManager = UserManager.getInstance();
    private InboxManager _inboxManager = InboxManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar actionBar = findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);

        _sessionMOauthProvider = (SessionMOauthProvider) SessionM.getAuthenticationProvider();
        _userManager = UserManager.getInstance();

        userBalanceTextView = findViewById(R.id.user_balance_textview);
        userBalanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserManager.getInstance().getCurrentUser() == null)
                    _sessionMOauthProvider.authenticateUser(SAMPLE_USER_EMAIL, SAMPLE_USER_PWD, new SessionMOauthProvider.SessionMOauthProviderListener() {
                        @Override
                        public void onAuthorize(SessionMOauthProvider.AuthenticatedState authenticatedState, SessionMError sessionMError) {
                            if (sessionMError != null) {
                                Toast.makeText(MainActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                fetchUser();
                            }
                        }
                    });
                else
                    _sessionMOauthProvider.logoutUser(new SessionMOauthProvider.SessionMOauthProviderListener() {
                        @Override
                        public void onAuthorize(SessionMOauthProvider.AuthenticatedState authenticatedState, SessionMError sessionMError) {
                            if (authenticatedState.equals(SessionMOauthProvider.AuthenticatedState.NotAuthenticated)) {
                                userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
                                fetchInboxMessages();
                            }
                        }
                    });
            }
        });
        _context = this;

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        messages = new ArrayList<>();
        _swipeMenuRecyclerView = findViewById(R.id.recycler_view);
        _swipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        _swipeMenuRecyclerView.setHasFixedSize(true);
        _swipeMenuRecyclerView.addItemDecoration(new ListViewDecoration());

        _swipeMenuRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        _swipeMenuRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);

        _menuAdapter = new MenuAdapter(messages);
        _menuAdapter.setOnItemClickListener(onItemClickListener);
        _swipeMenuRecyclerView.setAdapter(_menuAdapter);

        FloatingActionButton createNewMessageButton = findViewById(R.id.create_new_message_button);
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
                InboxManager.getInstance().createInboxMessage(newInboxMessage, new InboxManager.OnInboxMessageCreatedListener() {
                    @Override
                    public void onCreated(InboxMessage inboxMessage, SessionMError sessionMError) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (sessionMError != null) {
                            Toast.makeText(MainActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            fetchInboxMessages();
                            Toast.makeText(MainActivity.this, "New message created! \n ID: " + inboxMessage.getID(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (_sessionMOauthProvider.isAuthenticated())
            fetchUser();
    }

    private void fetchUser() {
        _userManager.fetchUser(new UserManager.OnUserFetchedListener() {
            @Override
            public void onFetched(SMPUser smpUser, Set<String> set, SessionMError sessionMError) {
                if (sessionMError != null) {
                    Toast.makeText(MainActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    if (smpUser != null) {
                        userBalanceTextView.setText(smpUser.getAvailablePoints() + "pts");
                    } else
                        userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
                }
                fetchInboxMessages();
            }
        });
    }

    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.item_height);

            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(_context)
                        .setBackgroundDrawable(R.drawable.selector_red)
//                        .setImage(R.mipmap.ic_action_delete)
                        .setText("Delete")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);

                SwipeMenuItem closeItem = new SwipeMenuItem(_context)
                        .setBackgroundDrawable(R.drawable.selector_blue)
                        .setText("Mark As Read")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(closeItem);

                SwipeMenuItem addItem = new SwipeMenuItem(_context)
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
            _inboxManager.updateInboxMessageState(messages.get(position), InboxMessage.STATE_TYPES.READ);
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
                _inboxManager.updateInboxMessageState(message, InboxMessage.STATE_TYPES.DELETED, new InboxManager.OnInboxMessagesStateUpdatedListener() {
                    @Override
                    public void onStateUpdated(List<InboxMessage> list, SessionMError sessionMError) {
                        _menuAdapter.notifyDataSetChanged();
                    }
                });
                messages.remove(adapterPosition);
                _menuAdapter.notifyItemRemoved(adapterPosition);
            }
            if (menuPosition == 1) {
                _inboxManager.updateInboxMessageState(message, InboxMessage.STATE_TYPES.READ, new InboxManager.OnInboxMessagesStateUpdatedListener() {
                    @Override
                    public void onStateUpdated(List<InboxMessage> list, SessionMError sessionMError) {
                        _menuAdapter.notifyDataSetChanged();
                    }
                });
            }
            if (menuPosition == 2) {
                _inboxManager.updateInboxMessageState(message, InboxMessage.STATE_TYPES.NEW, new InboxManager.OnInboxMessagesStateUpdatedListener() {
                    @Override
                    public void onStateUpdated(List<InboxMessage> list, SessionMError sessionMError) {
                        _menuAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    };

    @Override
    public void onRefresh() {
        fetchInboxMessages();
    }

    private SessionMError fetchInboxMessages() {
        return _inboxManager.fetchInboxMessages(new InboxManager.OnInboxMessagesFetchedListener() {
            @Override
            public void onFetched(List<InboxMessage> list, SessionMError sessionMError) {
                swipeRefreshLayout.setRefreshing(false);
                if (sessionMError != null) {
                    Toast.makeText(MainActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    if (messages == null) {
                        messages = new ArrayList<>();
                    } else {
                        messages.clear();
                    }
                    messages.addAll(list);
                    _menuAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
