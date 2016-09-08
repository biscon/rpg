package dk.bison.rpg.ui.weapon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.bison.rpg.R;
import dk.bison.rpg.core.weapon.WeaponTemplate;
import dk.bison.rpg.mvp.PresentationManager;

public class ChooseWeaponActivity extends AppCompatActivity implements ChooseWeaponMvpView {
    public static final String TAG = ChooseWeaponActivity.class.getSimpleName();
    ChooseWeaponPresenter presenter;

    @BindView(R.id.choose_weapon_items_rv)
    RecyclerView itemsRv;
    WeaponAdapter adapter;
    RecyclerView.LayoutManager itemsLayoutManager;
    List<WeaponTemplate> templates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_weapon);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Choose Weapon");
        setupRecyclerView();

        Intent i = getIntent();
        int slot = i.getIntExtra("slot", 0);
        // obtain instance to the presenter
        presenter = PresentationManager.instance().presenter(this, ChooseWeaponPresenter.class);
        presenter.setSlot(slot);
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
        adapter = new WeaponAdapter(templates);
        itemsRv.setAdapter(adapter);
    }

    @Override
    public void showTemplates(List<WeaponTemplate> templates) {
        this.templates.clear();
        this.templates.addAll(templates);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void closeView() {
        finish();
    }

    // Recyclerview stuff --------------------------------------------------------------------------
    public class WeaponAdapter extends RecyclerView.Adapter<WeaponViewHolder> {
        List<WeaponTemplate> dataset;
        public static final int TYPE_WEAPON = 0;

        public WeaponAdapter(List<WeaponTemplate> dataset) {
            this.dataset = dataset;
        }

        @Override
        public WeaponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(ChooseWeaponActivity.this).inflate(R.layout.viewholder_weapon, parent, false);
            // set the view's size, margins, paddings and layout parameters
            WeaponViewHolder vh = new WeaponViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final WeaponViewHolder vh, int position) {
            WeaponTemplate at = dataset.get(position);
            vh.nameTv.setText(at.getName());
            vh.dmgTv.setText(at.getDamage());
            vh.costTv.setText(String.format(Locale.US, "%s", at.getSizeAsString()));
            vh.weightTv.setText(String.format(Locale.US, "COST %d / WGT %d", at.getCost(), at.getWeight()));

            vh.weaponLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.onWeaponSelected(dataset.get(vh.getAdapterPosition()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }

        @Override
        public int getItemViewType(int position) {
            return TYPE_WEAPON;
        }
    }

    public class WeaponViewHolder extends RecyclerView.ViewHolder {
        public View root;
        public TextView nameTv;
        public TextView dmgTv;
        public TextView costTv;
        public TextView weightTv;
        public LinearLayout weaponLl;

        public WeaponViewHolder(View v) {
            super(v);
            root = v;
            nameTv = ButterKnife.findById(v, R.id.name_tv);
            dmgTv = ButterKnife.findById(v, R.id.dmg_tv);
            costTv = ButterKnife.findById(v, R.id.cost_tv);
            weightTv = ButterKnife.findById(v, R.id.weight_tv);
            weaponLl = ButterKnife.findById(v, R.id.weapon_ll);
        }
    }

}
