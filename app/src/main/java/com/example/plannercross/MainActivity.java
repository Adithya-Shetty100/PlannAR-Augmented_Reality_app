package com.example.plannercross;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.CamcorderProfile;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ArFragment arFragment;
    VideoRecorder videoRecorder;
    //android:name="com.google.ar.sceneform.ux.ArFragment"
    //<uses-permission android:name="android.permission.INTERNET"/>
    //<meta-data android:name="com.google.android.ar.API_KEY" android:value="AIzaSyDPcR_kKVXb9kiHqwEws73XwwRj453Xuaw" />

    private ModelRenderable modelRen,
                            balloonRen,
                            curtainRen,
                            flowerRen,
                            chairRen;

    ImageView model,balloon,curtain,flower,chair;

    View arrayView[];
    //ViewRenderable name_model; can be used for AR Text view

    int selected=1; //by default table model is chosen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);*/

        arFragment=(ArFragment)getSupportFragmentManager().findFragmentById(R.id.arFragment);

        //View
        model=(ImageView)findViewById(R.id.model);
        balloon=(ImageView)findViewById(R.id.balloon);
        curtain=(ImageView)findViewById(R.id.curtain);
        flower=(ImageView)findViewById(R.id.flower);
        chair=(ImageView)findViewById(R.id.chair);


        setArrayView();
        setClickListener(); //c2
        setUpModel();

        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {

                //when user tap on model, we will add model

                    Anchor anchor=hitResult.createAnchor();
                    AnchorNode anchorNode=new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    createModel(anchorNode,selected);



            }
        });



        /*arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            Anchor anchor=hitResult.createAnchor();
            ModelRenderable.builder()
                    .setSource(this, Uri.parse("model.sfb"))
                    .build()
                    .thenAccept(modelRenderable -> addModelToScene(anchor,modelRenderable))
                    .exceptionally(throwable -> {
                        AlertDialog.Builder builder=new AlertDialog.Builder(this);
                        builder.setMessage(throwable.getMessage())
                                .show();
                        return null;
                    });
        });*/

        Button button=(Button) findViewById(R.id.button);
        button.setOnClickListener(view->{

            if(videoRecorder==null){
                videoRecorder= new VideoRecorder();
                videoRecorder.setSceneView(arFragment.getArSceneView());

                int orientation=getResources().getConfiguration().orientation;
                videoRecorder.setVideoQuality(CamcorderProfile.QUALITY_HIGH,orientation);
            }

            boolean isRecording= videoRecorder.onToggleRecord(); //c4
            // Returns false if recording has stopped.
            //isRecording = videoRecorder.onToggleRecord();

            if(isRecording){
                button.setText("Stop ⏹");
                Toast.makeText(this,"Started Recording",Toast.LENGTH_SHORT).show();
            }
            else{
                button.setText("Record 📷");
                Toast.makeText(this,"Recording saved",Toast.LENGTH_SHORT).show();
            }


        } );
    }//onCreate ends here



    private void setUpModel() {

        ModelRenderable.builder()
                .setSource(this, Uri.parse("model.sfb"))
                .build()
                .thenAccept(renderable -> modelRen=renderable)
                .exceptionally(throwable ->{
                    Toast.makeText(this,"Unable to load table model",Toast.LENGTH_SHORT).show();
                            return null;
                }
                        );


        ModelRenderable.builder()
                .setSource(this, Uri.parse("balloon.sfb"))
                .build()
                .thenAccept(renderable -> balloonRen=renderable)
                .exceptionally(throwable ->{
                            Toast.makeText(this,"Unable to load balloon model",Toast.LENGTH_SHORT).show();
                            return null;
                        }
                );


        ModelRenderable.builder()
                .setSource(this, Uri.parse("stool.sfb"))
                .build()
                .thenAccept(renderable -> chairRen=renderable)
                .exceptionally(throwable ->{
                            Toast.makeText(this,"Unable to load chair model",Toast.LENGTH_SHORT).show();
                            return null;
                        }
                );



    }


    private void createModel(AnchorNode anchorNode, int selected) {

        if(selected==1){

            TransformableNode modelNode=new TransformableNode(arFragment.getTransformationSystem());
            modelNode.setParent(anchorNode);
            modelNode.setRenderable(modelRen);
            //arFragment.getArSceneView().getScene().addChild(anchorNode);
            modelNode.select();

        }

        if(selected==2){

            TransformableNode modelNode=new TransformableNode(arFragment.getTransformationSystem());
            modelNode.setParent(anchorNode);
            modelNode.setRenderable(balloonRen);
            //arFragment.getArSceneView().getScene().addChild(anchorNode);
            modelNode.select();

        }

        if(selected==5){

            TransformableNode modelNode=new TransformableNode(arFragment.getTransformationSystem());
            modelNode.setParent(anchorNode);
            modelNode.setRenderable(chairRen);
            //arFragment.getArSceneView().getScene().addChild(anchorNode);
            modelNode.select();

        }
    }


    private void setClickListener() {

        for(int i=0;i<arrayView.length;i++){
            arrayView[i].setOnClickListener(this); //c1
        }
    }


    private void setArrayView() {

        arrayView=new View[]{
                model,balloon,curtain,flower,chair
        };
    }

    @Override
    public void onResume(){

        super.onResume();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);



    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.model){
            selected=1;
            setBackground(view.getId());
        }

        else if(view.getId()==R.id.balloon){
            selected=2;
            setBackground(view.getId());
        }

        else if(view.getId()==R.id.curtain){
            selected=3;
            setBackground(view.getId());
        }

        else if(view.getId()==R.id.flower){
            selected=4;
            setBackground(view.getId());
        }

        else if(view.getId()==R.id.chair){
            selected=5;
            setBackground(view.getId());
        }



    }

    private void setBackground(int id) {

        for(int i=0;i<arrayView.length;i++)
        {
            if(arrayView[i].getId()==id)
                arrayView[i].setBackgroundColor(Color.parseColor("#80333639")); //selected model
            else
                arrayView[i].setBackgroundColor(Color.TRANSPARENT);

        }
    }



    /*private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {

        AnchorNode anchorNode=new AnchorNode(anchor);

        TransformableNode transformableNode=new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();
    }*/


}
