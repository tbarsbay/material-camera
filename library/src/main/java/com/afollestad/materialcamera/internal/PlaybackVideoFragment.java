package com.afollestad.materialcamera.internal;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.affolestad.easyvideoplayer.EasyVideoCallback;
import com.affolestad.easyvideoplayer.EasyVideoPlayer;
import com.afollestad.materialcamera.R;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * @author Aidan Follestad (afollestad)
 */
public class PlaybackVideoFragment extends Fragment implements CameraUriInterface, EasyVideoCallback {

    private EasyVideoPlayer mPlayer;
    private String mOutputUri;
    private String mVideoUrl;
    private BaseCaptureInterface mInterface;

    private Handler mCountdownHandler;
    private final Runnable mCountdownRunnable = new Runnable() {
        @Override
        public void run() {
            if (mPlayer != null) {
                long diff = mInterface.getRecordingEnd() - System.currentTimeMillis();
                if (diff <= 0) {
                    useVideo();
                    return;
                }
                if (mCountdownHandler != null)
                    mCountdownHandler.postDelayed(mCountdownRunnable, 200);
            }
        }
    };

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mInterface = (BaseCaptureInterface) activity;
    }

    public static PlaybackVideoFragment newInstance(String outputUri, String videoUrl,
                                                    boolean allowRetry, int primaryColor) {
        PlaybackVideoFragment fragment = new PlaybackVideoFragment();
        fragment.setRetainInstance(true);
        Bundle args = new Bundle();
        args.putString("output_uri", outputUri);
        args.putBoolean(CameraIntentKey.ALLOW_RETRY, allowRetry);
        args.putInt(CameraIntentKey.PRIMARY_COLOR, primaryColor);
        args.putString(CameraIntentKey.VIDEO_URL, videoUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer.reset();
            mPlayer = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mcam_fragment_videoplayback, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPlayer = (EasyVideoPlayer) view.findViewById(R.id.playbackView);
        mPlayer.setCallback(this);

        mPlayer.setSubmitTextRes(mInterface.labelUseVideo());
        mPlayer.setRetryTextRes(mInterface.labelRetry());
        mPlayer.setPromptText(mInterface.labelPrompt());
        mPlayer.setPlayDrawableRes(mInterface.iconPlay());
        mPlayer.setPauseDrawableRes(mInterface.iconPause());

        if (getArguments().getBoolean(CameraIntentKey.ALLOW_RETRY, true))
            mPlayer.setLeftAction(EasyVideoPlayer.LEFT_ACTION_RETRY);
        mPlayer.setRightAction(EasyVideoPlayer.RIGHT_ACTION_SUBMIT);

        mOutputUri = getArguments().getString("output_uri");
        mVideoUrl = getArguments().getString(CameraIntentKey.VIDEO_URL, null);

        if (mInterface.hasLengthLimit() && mInterface.shouldAutoSubmit() && mInterface.continueTimerInPlayback()) {
            startCountdownTimer();
        }

        if (mVideoUrl != null) {
            // There's a video url to play instead of a file uri
            mPlayer.setSource(Uri.parse(mVideoUrl));
        } else {
            mPlayer.setSource(Uri.parse(mOutputUri));
        }
    }

    private void startCountdownTimer() {
        if (mCountdownHandler == null)
            mCountdownHandler = new Handler();
        else mCountdownHandler.removeCallbacks(mCountdownRunnable);
        mCountdownHandler.post(mCountdownRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCountdownHandler != null) {
            mCountdownHandler.removeCallbacks(mCountdownRunnable);
            mCountdownHandler = null;
        }
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void useVideo() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        if (mInterface != null)
            mInterface.useVideo(mOutputUri);
    }

    @Override
    public String getOutputUri() {
        return getArguments().getString("output_uri");
    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {
    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {
    }

    @Override
    public void onBuffering(int percent) {
    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.mcam_error)
                .content(e.getMessage())
                .positiveText(android.R.string.ok)
                .show();
    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {
    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {
        if (mInterface != null)
            mInterface.onRetry(mOutputUri);
    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {
        useVideo();
    }
}