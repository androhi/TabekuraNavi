package jp.nfcgroup.tabekuranavi;

import jp.nfcgroup.tabekuranavi.fragment.ListFragment;
import jp.nfcgroup.tabekuranavi.fragment.MapFragment;
import jp.nfcgroup.tabekuranavi.view.KeywordHodler;
import jp.nfcgroup.tabekuranavi.view.MapView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainActivity extends BaseActivity implements OnClickListener {
    
    @SuppressWarnings("unused")
    private static final String TAG = "ListActivity";
    
    private FragmentManager mManager;
    private ListFragment mList;
    private MapFragment mMap;
    private ImageButton mChangeButton;
    private int mFragmentCase;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mManager = getFragmentManager();
        FragmentTransaction ft = mManager.beginTransaction();
        
        mMap = new MapFragment();
        mList = new ListFragment();
        ft.add(R.id.frame_list, mList);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
        mFragmentCase = 1;
        
        mKeywordHolder = new KeywordHodler(getApplicationContext(),(LinearLayout) findViewById(R.id.tag_holder),this);
        
        mChangeButton = (ImageButton) findViewById(R.id.change_view_button);
        mChangeButton.setBackgroundResource(R.drawable.button_tomap);
        mChangeButton.setOnClickListener(this);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

	protected void onUpdateViews(){
    	if(mFragmentCase == 0) {
    		mMap.updateViews();
    	} else if(mFragmentCase == 1) {
    		mList.updateViews();
    	}
    }
    
    public void onClick(View v) {
    	// Fragment入れ替え
    	FragmentTransaction ft = mManager.beginTransaction();
    	Fragment nowFragment = mManager.findFragmentById(R.id.frame_list);
    	if(nowFragment.equals(mMap)) {
    		ft.replace(R.id.frame_list, mList);
    	} else if(nowFragment.equals(mList)) {
    		ft.replace(R.id.frame_list, mMap);
    	}
    	ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    	ft.addToBackStack(null);
    	ft.commit();
    	
    	// 画面切替ボタンの画像入れ替え
    	if(nowFragment.equals(mMap)) {
    		mChangeButton.setBackgroundResource(R.drawable.button_tomap);
    		mFragmentCase = 1;
    	} else if(nowFragment.equals(mList)) {
    		mChangeButton.setBackgroundResource(R.drawable.button_tolist);
    		mFragmentCase = 0;
    	}
    }
}
