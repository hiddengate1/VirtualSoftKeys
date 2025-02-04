package tw.com.daxia.virtualsoftkeys.service.view;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.media.AudioManager;
import android.content.Context;

import tw.com.daxia.virtualsoftkeys.common.SPFManager;
import tw.com.daxia.virtualsoftkeys.service.ServiceFloating;

import static tw.com.daxia.virtualsoftkeys.common.Link.GOOGLE_APP_PACKAGE_NAME;
import static tw.com.daxia.virtualsoftkeys.common.Link.GOOGLE_PLAY_LINK;

/**
 * Created by daxia on 2017/4/26.
 */

public abstract class SoftKeyView {

    /*
     * View
     */
    protected View baseView;
    protected ImageButton IB_button_start, IB_button_end, IB_button_home;
    protected ServiceFloating accessibilityService;
    protected AudioManager audioManager;
    /*
     *  Listener
     */
    private View.OnTouchListener baseViewTouchListener;
    private View.OnClickListener softKeyEventClickListener;
    private View.OnLongClickListener softKeyEventLongClickListener;

    /*
     * Configure
     */
    protected boolean stylusOnlyMode;
    protected boolean reverseFunctionButton;

    /*
     * Device value
     */
    protected int softkeyBarHeight;


    public SoftKeyView(ServiceFloating accessibilityService) {
        init(accessibilityService);
        loadConfigure();
        initBaseView();
        initImageButton();
        initBaseViewTheme();
        initTouchEvent();
        setSoftKeyEvent();
        audioManager = (AudioManager) accessibilityService.getSystemService(Context.AUDIO_SERVICE);
    }

    /*
     * The concrete method
     */



    /**
     * Link the base view & find the button view
     */
    abstract void initBaseView();

    /**
     * set the base view theme
     */
     abstract void initBaseViewTheme();
    /**
     * set the button
     */
    abstract void initImageButton();

    /**
     * Init Touch event for close the softkey bar
     */
    abstract void initTouchEvent();



    public abstract WindowManager.LayoutParams getLayoutParamsForLocation();


    private void init(ServiceFloating accessibilityService) {
        this.accessibilityService = accessibilityService;
    }

    /**
     * Get all configure from SPF.
     * It is also for refresh SPF or input new SPF.
     */
    public void refresh(){
        loadConfigure();
        initImageButton();
        initBaseViewTheme();
    }


    private void loadConfigure() {
        this.reverseFunctionButton = SPFManager.getReverseFunctionButton(accessibilityService);
        this.stylusOnlyMode = SPFManager.getStylusOnlyMode(accessibilityService);
    }

    private void setSoftKeyEvent() {
        softKeyEventClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add HapticFeedback
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                //Click event
                if (v.getId() == IB_button_start.getId()) {
                    if (reverseFunctionButton) {
                        audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
                    } else {
                        accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                } else if (v.getId() == IB_button_home.getId()) {
                    if (reverseFunctionButton) {
                        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                    } else {
                        audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
                    }
                } else if (v.getId() == IB_button_end.getId()) {
                    if (reverseFunctionButton) {
                        accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    } else {
                        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                    }
                }

                accessibilityService.hiddenSoftKeyBar(false);
            }
        };
        softKeyEventLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v.getId() == IB_button_start.getId()) {
                    accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG);
                } else if (v.getId() == IB_button_home.getId()) {
                    accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                } else if (v.getId() == IB_button_end.getId()) {
                    if (!reverseFunctionButton) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);
                        }
                    }
                }
                //Only trigger long click
                return true;
            }
        };

        //Set the click listener
        IB_button_start.setOnClickListener(softKeyEventClickListener);
        IB_button_home.setOnClickListener(softKeyEventClickListener);
        IB_button_end.setOnClickListener(softKeyEventClickListener);

        //Set the long click listener
        IB_button_start.setOnLongClickListener(softKeyEventLongClickListener);
        IB_button_home.setOnLongClickListener(softKeyEventLongClickListener);
        IB_button_end.setOnLongClickListener(softKeyEventLongClickListener);
    }


    /*
     * The  public  method
     */
    public View getBaseView() {
        return baseView;
    }


}
