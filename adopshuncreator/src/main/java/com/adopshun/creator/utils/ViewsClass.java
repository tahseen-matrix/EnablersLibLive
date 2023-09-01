package com.adopshun.creator.utils;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ViewsClass {

    public static int getChildrenViews(ViewGroup parent){
        int count = parent.getChildCount();
        for (int i=0;i<parent.getChildCount();i++){
            if (parent.getChildAt(i) instanceof ViewGroup){
                count+=getChildrenViews((ViewGroup) parent.getChildAt(i));
            }
        }
        return count;
    }

    public static int countChild(View view) {
        if (!(view instanceof ViewGroup))
            return 1;

        int counter = 0;

        ViewGroup viewGroup = (ViewGroup) view;

        for (int i=0; i<viewGroup.getChildCount(); i++) {
            counter += countChild(viewGroup.getChildAt(i));
        }

        return counter;
    }

     public static  ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup vg = (ViewGroup) v;
        for (int i = 0; i < vg.getChildCount(); i++) {

            View child = vg.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }

        return result;
    }

    public static <T> ArrayList<T> getViewsFromViewGroup(View root, Class<T> clazz) {
        ArrayList<T> result = new ArrayList<T>();
        for (View view : getAllViewsFromRoots(root))
            if (clazz.isInstance(view))
                result.add(clazz.cast(view));
        return result;
    }

    public static ArrayList<View> getAllViewsFromRoots(View...roots) {
        ArrayList<View> result = new ArrayList<View>();
        for (View root : roots)
            getAllViews(result, root);
        return result;
    }

    private static void getAllViews(ArrayList<View> allviews, View parent) {
        allviews.add(parent);
        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup)parent;
            for (int i = 0; i < viewGroup.getChildCount(); i++)
                getAllViews(allviews, viewGroup.getChildAt(i));
        }
    }
}
