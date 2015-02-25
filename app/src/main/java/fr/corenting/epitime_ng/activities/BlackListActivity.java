package fr.corenting.epitime_ng.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.adapters.BlacklistAdapter;
import fr.corenting.epitime_ng.utils.ThemeUtils;

public class BlackListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTheme(ThemeUtils.getTheme(this));
        EpiTime.getInstance().setCurrentActivity(this);
        setContentView(R.layout.activity_blacklist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ListView blacklist = (ListView) this.findViewById(R.id.blacklist);
        blacklist.setAdapter(new BlacklistAdapter());
        blacklist.setEmptyView(findViewById(android.R.id.empty));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
