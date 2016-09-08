package dk.bison.rpg.ui.armor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.bison.rpg.R;
import dk.bison.rpg.core.armor.ArmorTemplate;
import dk.bison.rpg.core.character.CharacterStats;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.ui.character.CreateCharacterMvpView;
import dk.bison.rpg.ui.character.CreateCharacterPresenter;

public class ChooseArmorActivity extends AppCompatActivity implements ChooseArmorMvpView {
    public static final String TAG = ChooseArmorActivity.class.getSimpleName();
    ChooseArmorPresenter presenter;

    @BindView(R.id.choose_armor_items_rv)
    RecyclerView itemsRv;
    ArmorAdapter adapter;
    RecyclerView.LayoutManager itemsLayoutManager;
    List<ArmorTemplate> templates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_armor);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Choose Armor");
        setupRecyclerView();
        // obtain instance to the presenter
        presenter = PresentationManager.instance().presenter(this, ChooseArmorPresenter.class);
        presenter.clearFilter();
        Intent i = getIntent();
        int sort_type = i.getIntExtra("type", -1);
        if(sort_type != -1)
            presenter.filterType(sort_type);
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
        templates = new ArrayList<>();
        adapter = new ArmorAdapter(templates);
        itemsRv.setAdapter(adapter);
    }

    @Override
    public void showTemplates(List<ArmorTemplate> templates) {
        this.templates.clear();
        this.templates.addAll(templates);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void closeView() {
        finish();
    }

    // Recyclerview stuff --------------------------------------------------------------------------
    public class ArmorAdapter extends RecyclerView.Adapter<ArmorViewHolder> {
        List<ArmorTemplate> dataset;
        public static final int TYPE_ARMOR = 0;

        public ArmorAdapter(List<ArmorTemplate> dataset) {
            this.dataset = dataset;
        }

        @Override
        public ArmorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(ChooseArmorActivity.this).inflate(R.layout.viewholder_armor, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ArmorViewHolder vh = new ArmorViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ArmorViewHolder vh, int position) {
            ArmorTemplate at = dataset.get(position);
            vh.nameTv.setText(at.getName());
            vh.acTv.setText(String.format(Locale.US, "AC %d", at.getAC()));
            vh.costTv.setText(String.format(Locale.US, "%s", at.getTypeAsString()));
            vh.weightTv.setText(String.format(Locale.US, "COST %d / WGT %d", at.getCost(), at.getWeight()));
            vh.armorLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.onArmorSelected(dataset.get(vh.getAdapterPosition()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }

        @Override
        public int getItemViewType(int position) {
            return TYPE_ARMOR;
        }
    }

    public class ArmorViewHolder extends RecyclerView.ViewHolder {
        public View root;
        public TextView nameTv;
        public TextView acTv;
        public TextView costTv;
        public TextView weightTv;
        public LinearLayout armorLl;

        public ArmorViewHolder(View v) {
            super(v);
            root = v;
            nameTv = ButterKnife.findById(v, R.id.name_tv);
            acTv = ButterKnife.findById(v, R.id.ac_tv);
            costTv = ButterKnife.findById(v, R.id.cost_tv);
            weightTv = ButterKnife.findById(v, R.id.weight_tv);
            armorLl = ButterKnife.findById(v, R.id.armor_ll);
        }
    }

}
