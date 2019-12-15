package com.montassar.timetoguessbasketball.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.montassar.timetoguessbasketball.R;
import com.montassar.timetoguessbasketball.database.DBHelper;
import com.montassar.timetoguessbasketball.utils.LetterAdapter;
import com.montassar.timetoguessbasketball.utils.SoundsManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class GameFragment extends Fragment implements RewardedVideoAdListener {

    public static final String KEY_LETTER_GAME = "letter";
    public static final String KEY_SPACE_GAME = "space";
    public static final String KEY_LETTER_POS = "letter_pos";
    public static final String KEY_SPACE_POS = "space_pos";
    private DBHelper database;
    private RewardedVideoAd mRewardedVideoAd;
    private InterstitialAd mInterstitialAd;


    private ImageView imgPlayerHolder;
    int coins;

    private final String TAG="GameFragment";
    private SoundsManager sou;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;

    private Dialog mDialog;


    private TextView txtCurrentLevel,coinsValue;
    private int currentCoinsNumber,playerID;
    private String quizTest;
    int countSpaces;
    int playerTries = 0;
    int result;
    int playerPoints;
    int sWidth,sHeight;
    double screenInches;
    private ImageView imgBombHelp,imgSolutionHelp;



    GridView gridLetters;
    ArrayList<HashMap<String,String>> lettersArrray,spacesArray;
    char[] alphabetLettersArray,alphaberSpacesArray;
    TextView[] spaceViews;
    HashMap<String,String> lettersMap, spacesMap;
    LinearLayout spaceGrid1,spaceGrid2,spaceGrid3,spaceGrid4;
    LetterAdapter letterAdapter;
    ArrayList<HashMap<String,String>> positionsArray;
    HashMap<String, String> positionsMap;
    Animation animShake,animBlink;
    View view;
    int globalViewId;
    ImageView imgStore;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_game,container,false);
        sou = new SoundsManager(getContext());

        database = new DBHelper(getContext());
        //=============Navigation for Back Button =============
        ImageView imgBack =(ImageView) view.findViewById(R.id.img_back_game);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: back Button. ");
                sou.playSound(R.raw.play);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        //=====================================================
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        sWidth = displaymetrics.widthPixels;
        sHeight = displaymetrics.heightPixels;
        int dens = displaymetrics.densityDpi;
        double wi = (double) sWidth / (double) dens;
        double hi = (double) sHeight / (double) dens;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        screenInches = Math.sqrt(x + y);

        imgPlayerHolder = (ImageView) view.findViewById(R.id.img_player_holder);
        gridLetters = (GridView) view.findViewById(R.id.grid_letters);

        //========= banner add ===============

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        AdView mBannerAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mBannerAdView.loadAd(adRequest);
        mBannerAdView.setAdListener(new AdListener());

        //---------------------------------
        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdListener(new AdListener());
        mInterstitialAd.setAdUnitId("ca-app-pub-3861349095858898/2257005209");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        //------------- video reward
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getContext());
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        //==========================




        spaceGrid1 = (LinearLayout) view.findViewById(R.id.spacesGrid1);
         spaceGrid2 = (LinearLayout) view.findViewById(R.id.spacesGrid2);
         spaceGrid3 = (LinearLayout) view.findViewById(R.id.spacesGrid3);
         spaceGrid4 = (LinearLayout) view.findViewById(R.id.spacesGrid4);

        mSharedPreferences = getContext().getSharedPreferences("MyPref",0);
        editor = mSharedPreferences.edit();
        String imageNameFromDB;
        txtCurrentLevel = (TextView) view.findViewById(R.id.txt_current_level);
        coinsValue = (TextView) view.findViewById(R.id.txt_coins_value);

        currentCoinsNumber =  database.getCoinsNum();
        coinsValue.setText(""+getCoinsNumber());

        playerID = database.getNextPlayerID();
        txtCurrentLevel.setText("Quiz "+playerID);
        //====================================
        imgStore = (ImageView) view.findViewById(R.id.img_coins_add);
        imgStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sou.playSound(R.raw.play);
                showStoreDialog(getContext());
            }
        });
        //====================================

        try {
            DBHelper db = new DBHelper(getContext());
            quizTest =  db.getAnswer(playerID);
            imageNameFromDB = db.getImageName(playerID);
            generateImage(imageNameFromDB);
            generateLetters(quizTest);
            generateSpaces(quizTest);

        }catch (Exception e){
            e.printStackTrace();
            Log.i(TAG, "onCreateView: "+e.getMessage());
            // Toast.makeText(getContext(), "PlayFragment: error in geting answer: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        imgBombHelp = (ImageView) view.findViewById(R.id.img_bomb_help);
        imgSolutionHelp = (ImageView) view.findViewById(R.id.img_solution_help);
        imgBombHelp.setOnClickListener(helpClickHandler);
        imgSolutionHelp.setOnClickListener(helpClickHandler);




        return view;
    }
    public void showInterstitialAd() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }else {

                    //Toast.makeText(getContext(), "not loaded yet", Toast.LENGTH_SHORT).show();
                }

            }

        }, 3000);
    }
    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    private View.OnClickListener helpClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sou.playSound(R.raw.button);
            getHelp(v.getId());

        }
    };
    public void getHelp(final int viewId) {
        int remainCoins = Integer.parseInt(coinsValue.getText().toString());

        if (isHelpUsed(viewId) != 1) {
            boolean noLetterCoins = (viewId == R.id.img_bomb_help && remainCoins < 5);
            boolean noSolutionCoins = (viewId == R.id.img_solution_help && remainCoins < 10);
            String msg = "";
            if (noLetterCoins || noSolutionCoins) {
                msg = "you don't have enough coins";
                showHelpsResultDialog(getContext(), msg);
            } else {


                switch (viewId) {

                    case R.id.img_bomb_help:
                        msg = "It Will Cost You 5 coins.";
                        break;
                    case R.id.img_solution_help:
                        msg = "It Will Cost You 10 coins.";
                        break;
                }

                globalViewId = viewId;
                showHelpsDialog(getContext(), msg);
            }
        }else {
            executeHelp(viewId);
        }

    }
    private void showStoreDialog(Context context)
    {
        mDialog = new Dialog(context);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.popup_store);
        mDialog.setCanceledOnTouchOutside(true);
        ImageView imgBackX = (ImageView) mDialog.findViewById(R.id.img_back_x);
        imgBackX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        TextView txtStoreWatch = (TextView) mDialog.findViewById(R.id.txt_state_reward);

        if (mRewardedVideoAd.isLoaded())
        {
            txtStoreWatch.setText("click me to watch");
            txtStoreWatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRewardedVideoAd.show();



                }
            });
        }else {
            txtStoreWatch.setText("no video loaded yet");
        }



        mDialog.show();
    }
    private void showHelpsResultDialog(Context context,String resultTxt)
    {
        mDialog = new Dialog(context);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.popup_helps_result);
        mDialog.setCanceledOnTouchOutside(false);
        TextView txtHelps = (TextView) mDialog.findViewById(R.id.txt_result);
        txtHelps.setText(resultTxt);
        ImageView imgDismiss = (ImageView) mDialog.findViewById(R.id.img_dismiss_solution);
        imgDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }
    private void showHelpsDialog(Context context, String helpsTxt)
    {
        mDialog = new Dialog(context);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.popup_helps);
        mDialog.setCanceledOnTouchOutside(false);
        TextView txtHelps = (TextView) mDialog.findViewById(R.id.txt_helps);
        txtHelps.setText(helpsTxt);
        ImageView imgYes = (ImageView) mDialog.findViewById(R.id.img_yes_helps);
        ImageView imgNo = (ImageView) mDialog.findViewById(R.id.img_no_helps);
        imgNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        imgYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toast.makeText(getContext(), "get helps", Toast.LENGTH_SHORT).show();
                String coins = "0";
                switch (globalViewId)
                {
                    case R.id.img_solution_help:
                        coins = "10";
                        break;
                    case R.id.img_bomb_help:
                        coins = "5";
                        break;
                }
                database.addUsedCoins(coins);
                coinsValue.setText(String.valueOf(getCoinsNumber()));
                mDialog.dismiss();
                executeHelp(globalViewId);

            }
        });
        mDialog.show();
    }

    public int isHelpUsed(int viewId)
    {
        int state = 0;
        Cursor c = database.getHelpState(playerID);
        if (c.getCount() !=0)
        {
            switch (viewId)
            {
                case R.id.img_bomb_help:
                    state = c.getInt(c.getColumnIndex(DBHelper.HELPS_HIDE));
                    break;
                case R.id.img_solution_help:
                    state = c.getInt(c.getColumnIndex(DBHelper.HELPS_SOLUTION));
                    break;
            }
        }
        return state;
    }
    public void  executeHelp(int viewId)
    {
        switch (viewId)
        {
            case R.id.img_bomb_help:
                sou.playSound(R.raw.bomb);
                database.updateHelpState(String.valueOf(playerID),DBHelper.HELPS_HIDE);
                //TODO blink animation and sound effects
                animBlink =AnimationUtils.loadAnimation(getContext(),R.anim.blink);
                imgBombHelp.setSelected(true);
                imgBombHelp.setEnabled(false);

                for (int i=0; i<lettersArrray.size();i++)
                {
                    if (lettersArrray.get(i).get("is_real").equals("0"))
                    {
                        for (int x=0; x < positionsArray.size();x++)
                        {
                            if (positionsArray.get(x).get(KEY_LETTER_POS).equals(String.valueOf(i)))
                            {
                                String spacePos = positionsArray.get(x).get(KEY_SPACE_POS);

                                TextView spaceHide = (TextView) spaceViews[Integer.parseInt(spacePos)];
                                spaceHide.setText("");
                                positionsArray.remove(x);
                            }
                        }
                        //TODO Sounds
                        gridLetters.getChildAt(i).setAnimation(animBlink);
                        gridLetters.getChildAt(i).setVisibility(View.INVISIBLE);
                    }
                }

                break;
            case R.id.img_solution_help:
                database.updateHelpState(String.valueOf(playerID),DBHelper.HELPS_SOLUTION);
                showHelpsResultDialog(getContext(),"Solution : "+quizTest);

                break;
        }
    }

    public void generateSpaces(String quizText)
    {
        alphaberSpacesArray = quizText.toCharArray();
        spacesArray = new ArrayList<HashMap<String, String>>();
        spaceViews = new TextView[quizText.length()];

        for (int i=0; i < quizText.length(); i++)
        {
            spacesMap = new HashMap<String, String>();
            spacesMap.put(KEY_SPACE_GAME,Character.toString(quizText.charAt(i)));
            spacesArray.add(spacesMap);
            spaceViews[i] = new TextView(getContext());

            // ======= stuff for resizing the textViews ========
            Configuration config = getResources().getConfiguration();
            int width = 0, height = 0, textSize = 0;
            if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
                width = 30;
                height = 30;
                textSize = 24;
            }else if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
                width = 30;
                height = 30;
                textSize = 24;
                // Toast.makeText(getContext(), "I="+sWidth, Toast.LENGTH_LONG).show();
                Log.i(TAG, "generateSpaces: "+sWidth);
                if (sWidth>= 480 && screenInches >= 4 && screenInches <= 6.5) {
                    width = 80;
                    height =80;
                }
                if (sWidth>=1080)
                {
                    width = 110;
                    height =110;
                }
                if (sWidth>=1440)
                {
                    width = 140;
                    height =140;
                }
            } else if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                width = 80;
                height = 80;
                textSize = 32;

                if (screenInches > 6.5 && screenInches < 9) {
                    width = 120;
                    height = 120;
                    textSize = 40;
                }
            } else if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                width = 100;
                height = 96;
                textSize = 56;
            } else {
                width = 60;
                height = 60;
                textSize = 40;
            }


            spaceViews[i].setLayoutParams(new LinearLayout.LayoutParams(width,height));
            spaceViews[i].setGravity(Gravity.CENTER);
            spaceViews[i].setTextColor(Color.WHITE);
            //==================================================
            if (!Character.toString(quizText.charAt(i)).equals(" "))
            {
                spaceViews[i].setBackgroundResource(R.drawable.letter_space);
                spaceViews[i].setTextSize(2,textSize);
                //spaceViews[i].setPadding(5,5,5,5);
                spaceViews[i].setText("");


            }else {
                spaceViews[i].setLayoutParams(new LinearLayout.LayoutParams(width/3,height));
                spaceViews[i].setBackgroundColor(Color.TRANSPARENT);
                spaceViews[i].setPadding(0,0,0,0);
                spaceViews[i].setVisibility(View.INVISIBLE);
                spaceViews[i].setText(" ");
            }
            if (i < 7)
            {
                spaceGrid1.addView(spaceViews[i]);
            }else if ( i < 14)
            {
                spaceGrid2.addView(spaceViews[i]);
            }else if (i < 32 )
            {
                spaceGrid3.addView(spaceViews[i]);
            }
            spaceViews[i].setOnClickListener(new spacesItemClickHandler(i));
        }
    }


    public void generateLetters(String quizText)
    {

        String alphabet = getResources().getString(R.string.alphapbet);
        int nbrLetters = 14;
        countSpaces = quizText.length() - quizText.replace(" ","").length();
        alphabetLettersArray = quizText.toCharArray();
        lettersArrray = new ArrayList<HashMap<String, String>>();
        gridLetters.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        quizText = quizText.replace(" ","");
        for (int i=0; i < quizText.length(); i++)
        {
            lettersMap = new HashMap<String, String>();
            lettersMap.put(KEY_LETTER_GAME,Character.toString(quizText.charAt(i)));
            lettersMap.put("is_real","1");
            lettersArrray.add(lettersMap);

            if ( i == quizText.length()-1)
            {
                if ( quizText.length() >= nbrLetters)
                {
                    nbrLetters = quizText.length() + 4 ;
                }
                for ( int x = quizText.length(); x < nbrLetters; x++)
                {
                    Random random = new Random();
                    int chNum = random.nextInt(alphabet.length());
                    lettersMap = new HashMap<String, String>();
                    lettersMap.put(KEY_LETTER_GAME,Character.toString(alphabet.charAt(chNum)));
                    lettersMap.put("is_real","0");
                    lettersArrray.add(lettersMap);
                }

            }
        }
        Collections.shuffle(lettersArrray);
        letterAdapter = new LetterAdapter(getContext(),lettersArrray);
        gridLetters.setAdapter(letterAdapter);

        positionsArray = new ArrayList<HashMap<String, String>>();
        gridLetters.setOnItemClickListener(lettersItemClickHandler);
    }
    private GridView.OnItemClickListener lettersItemClickHandler = new GridView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //Toast.makeText(getContext(),position+"",Toast.LENGTH_SHORT).show();
            if (spaceViews.length > positionsArray.size() + countSpaces) {
                gridLetters.getChildAt(position).setVisibility(View.INVISIBLE);

                sou.playSound(R.raw.space);

                addLetters(position);
            }
        }
    };
    private void addLetters (int position)
    {
        for (int i= 0; i < spaceViews.length; i++)
        {
            TextView spaceIndex = (TextView) spaceViews[i];
            if (spaceIndex.getVisibility() == View.INVISIBLE)
            {
                continue;
            }
            if (spaceIndex.getText().equals("")|| spaceIndex.getText().equals("?"))
            {
                spaceIndex.setText(lettersArrray.get(position).get(KEY_LETTER_GAME).toUpperCase());
                positionsMap = new HashMap<String, String>();
                positionsMap.put(KEY_LETTER_POS,String.valueOf(position));
                positionsMap.put(KEY_SPACE_POS,String.valueOf(i));
                positionsArray.add(positionsMap);

                checkIfFinal();
                break;
            }
        }
    }
    private void checkIfFinal()
    {
        if (spaceViews.length == positionsArray.size() + countSpaces)
        {
            for (int x = 0; x < spaceViews.length; x++)
            {
                TextView spaceFinal = (TextView) spaceViews[x];
                if (!spaceFinal.getText().toString().equals(String.valueOf(alphaberSpacesArray[x]).toUpperCase()))
                {
                    if ( playerTries < 4 )
                    {
                        playerTries++;
                        database.setTries(playerID,playerTries);
                        result = 0;
                    }
                    break;
                }else{
                    if (x == spaceViews.length -1)
                    {
                        playerPoints = 0;
                        switch (playerTries) {
                            case 0:
                                playerPoints = 100;
                                break;

                            case 1:
                                playerPoints = 80;
                                break;

                            case 2:
                                playerPoints = 60;
                                break;

                            case 3:
                                playerPoints = 40;
                                break;

                            case 4:
                                playerPoints = 20;
                                break;
                        }
                        result = 1;
                    }
                }
            }
            isRight(result);
        }
    }
    private void isRight(int result) {

        editor.putInt("playingNum",mSharedPreferences.getInt("playingNum",0) + 1);
        editor.commit();

        if (result == 1)
        {
            // Toast.makeText(getContext(),"Right Answer",Toast.LENGTH_SHORT).show();
            //TODO Stuff for sound, dataBase level complete, Right answer Dialog

            //TODO addPoints(); addCoins();
            addCoins();
            database.setPlayerCompleted(playerID);
            sou.playSound(R.raw.right);
            showRightDialog(getContext());




        }else if (result == 0)
        {
            //Toast.makeText(getContext(),"Wrong Answer",Toast.LENGTH_SHORT).show();
            if (mSharedPreferences.getInt("vibrate",1) == 1)
            {
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(500);




            }
            RelativeLayout playerLayout = (RelativeLayout) view.findViewById(R.id.layout_player);
            animShake = AnimationUtils.loadAnimation(getContext(),R.anim.shake);
            playerLayout.startAnimation(animShake);
            //TODO stuff for sound, Wrong Dialog and Ads
            if (mSharedPreferences.getInt("playingNum", 0) >= 5) {
                showInterstitialAd();
                editor.putInt("playingNum", 0);
                editor.commit();
            }
            sou.playSound(R.raw.wrong);
        }
    }
    private void showRightDialog(Context context)
    {
        mDialog = new Dialog(context);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.popup_right_answer);
        //mDialog.set
        ImageView imgBackMenu = (ImageView) mDialog.findViewById(R.id.img_menu);
        ImageView imgBackNext = (ImageView) mDialog.findViewById(R.id.img_next);
        RelativeLayout rlRight = (RelativeLayout) mDialog.findViewById(R.id.popup_right_answer);
        animShake = AnimationUtils.loadAnimation(getContext(),R.anim.shake);
        rlRight.setAnimation(animShake);
        imgBackMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        imgBackNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                // Toast.makeText(getContext(), "to the next question", Toast.LENGTH_SHORT).show();
                GameFragment fragment = new GameFragment();
                FragmentTransaction transaction =getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,fragment);
                transaction.commit();
            }
        });
        mDialog.show();
    }
    private void addCoins() {
        coins = 0;
        if (playerPoints == 100) {
            coins = 2;

        } else if (playerPoints > 0 && playerPoints < 100) {
            coins = 1;

        }
        database.addTotalCoins(coins);

        coinsValue.setText(String.valueOf(getCoinsNumber()));

    }




    public void generateImage(String img)
    {
        String imageFilePath = "file:///android_asset/images/";
        Picasso picasso = new Picasso.Builder(getContext()).listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Log.i(TAG, "onImageLoadFailed: generateImage");
                //Toast.makeText(getContext(), "failed to load image", Toast.LENGTH_SHORT).show();
            }
        }).build();
        picasso.load(imageFilePath+img).error(R.drawable.failed).placeholder(R.drawable.placeholder).into(imgPlayerHolder, new Callback() {
            @Override
            public void onSuccess() {
                //  Toast.makeText(getContext(), "image loaded successfully", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onSuccess: generate image successfully.");
            }

            @Override
            public void onError(Exception e) {
                Log.i(TAG, "onError: generate image unsuccessful: "+e.getMessage());
                e.printStackTrace();

            }
        });
    }


    private int getCoinsNumber() {
        Cursor cCoins = database.getCoinsCount();

        int  coinsNumber = cCoins.getInt(cCoins.getColumnIndex("total_coins")) - cCoins.getInt(cCoins.getColumnIndex("used_coins"));
        return coinsNumber;
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        database.addRewardedCoins("15");
        coinsValue.setText(""+getCoinsNumber());
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    public class spacesItemClickHandler implements View.OnClickListener{

        private final int position;
        public spacesItemClickHandler(int position)
        {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            TextView spaceIndex = (TextView) spaceViews[position];
            if (!spaceIndex.getText().equals("")) {
                for (int i = 0; i < positionsArray.size(); i++) {

                    if (positionsArray.get(i).get(KEY_SPACE_POS).equals(String.valueOf(position))) {
                        int letterPos = Integer.parseInt(positionsArray.get(i).get(KEY_LETTER_POS));
                        gridLetters.getChildAt(letterPos).setVisibility(View.VISIBLE);

                        spaceIndex.setText("");
                        sou.playSound(R.raw.space);

                        positionsArray.remove(i);

                        break;
                    }

                }
            }
        }
    }

    //=============================================================================

}
