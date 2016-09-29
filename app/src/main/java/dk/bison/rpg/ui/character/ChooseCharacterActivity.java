package dk.bison.rpg.ui.character;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.bison.rpg.AppState;
import dk.bison.rpg.BaseActivity;
import dk.bison.rpg.R;
import dk.bison.rpg.core.character.Character;
import dk.bison.rpg.mvp.MvpPendingEvent;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.util.Snacktory;

public class ChooseCharacterActivity extends BaseActivity implements ChooseCharacterMvpView {
    public static final String TAG = ChooseCharacterActivity.class.getSimpleName();
    ChooseCharacterPresenter presenter;

    @BindView(R.id.choose_character_cl)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.choose_character_create_fab)
    FloatingActionButton createFab;

    @BindView(R.id.choose_character_items_rv)
    RecyclerView itemsRv;
    CharacterAdapter adapter;
    RecyclerView.LayoutManager itemsLayoutManager;
    List<Character> characters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_character);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Choose Character");
        setupRecyclerView();
        // obtain instance to the presenter
        presenter = PresentationManager.instance().presenter(this, ChooseCharacterPresenter.class);
        /*
        Intent i = getIntent();
        int sort_type = i.getIntExtra("type", -1);
        if(sort_type != -1)
            presenter.filterType(sort_type);
            */
        createFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharacterEditEvent event = new CharacterEditEvent(null);
                PresentationManager.instance().publishEvent(new MvpPendingEvent(EditCharacterPresenter.class, event, MvpPendingEvent.DELIVER_ON_CREATE));
                Intent i = new Intent(ChooseCharacterActivity.this, EditCharacterActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attachView(this);
    }

    @Override
    protected void onPause() {
        presenter.detachView();
        super.onPause();
    }

    private void setupRecyclerView() {
//        countryRv.setHasFixedSize(true);
        itemsLayoutManager = new LinearLayoutManager(this);
        itemsRv.setLayoutManager(itemsLayoutManager);
        characters = new ArrayList<>();
        adapter = new CharacterAdapter(characters);
        itemsRv.setAdapter(adapter);
    }

    @Override
    public void showCharacters(List<Character> characters) {
        this.characters.clear();
        this.characters.addAll(characters);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void closeView() {
        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Choose action");
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 1, "Delete");

    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Delete") {
            //Toast.makeText(this, "Character deleted", Toast.LENGTH_SHORT).show();
            Character c = characters.get(item.getItemId());
            Snacktory.showMessage(coordinatorLayout, "Character '" + c.getName() + "' deleted", Snackbar.LENGTH_SHORT);
            presenter.deleteCharacter(ChooseCharacterActivity.this, c);
            return true;
        }
        if (item.getTitle() == "Edit") {
            Character c = characters.get(item.getItemId());
            CharacterEditEvent event = new CharacterEditEvent(c);
            PresentationManager.instance().publishEvent(new MvpPendingEvent(EditCharacterPresenter.class, event, MvpPendingEvent.DELIVER_ON_CREATE));
            Intent i = new Intent(ChooseCharacterActivity.this, EditCharacterActivity.class);
            startActivity(i);
            return true;
        }
        return false;
    }

    // Recyclerview stuff --------------------------------------------------------------------------
    public class CharacterAdapter extends RecyclerView.Adapter<CharacterViewHolder> {
        List<Character> dataset;
        public static final int TYPE_CHARACTER = 0;

        public CharacterAdapter(List<Character> dataset) {
            this.dataset = dataset;
        }

        @Override
        public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(ChooseCharacterActivity.this).inflate(R.layout.viewholder_character, parent, false);
            // set the view's size, margins, paddings and layout parameters
            CharacterViewHolder vh = new CharacterViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final CharacterViewHolder vh, int position) {
            Character c = dataset.get(position);
            vh.nameTv.setText(c.getName());
            vh.levelTv.setText(String.format(Locale.US, "Level %d", c.getLevel()));
            vh.xpTv.setText(String.format(Locale.US, "%d XP (%d)", c.getXP(), c.getCharClass().getXPForLevel(c.getLevel()+1)));
            vh.moneyTv.setText(String.format(Locale.US, "Gold %d", c.getMoney()));

            vh.charLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.onCharacterSelected(dataset.get(vh.getAdapterPosition()));
                }
            });

            vh.charLl.setId(position);
            registerForContextMenu(vh.charLl);
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }

        @Override
        public int getItemViewType(int position) {
            return TYPE_CHARACTER;
        }
    }

    public class CharacterViewHolder extends RecyclerView.ViewHolder {
        public View root;
        public TextView nameTv;
        public TextView levelTv;
        public TextView xpTv;
        public TextView moneyTv;
        public LinearLayout charLl;

        public CharacterViewHolder(View v) {
            super(v);
            root = v;
            nameTv = ButterKnife.findById(v, R.id.name_tv);
            levelTv = ButterKnife.findById(v, R.id.level_tv);
            xpTv = ButterKnife.findById(v, R.id.xp_tv);
            moneyTv = ButterKnife.findById(v, R.id.money_tv);
            charLl = ButterKnife.findById(v, R.id.char_ll);
        }
    }

}
