package fiu.com.skillcourt.ui.dynamicsteps;

import android.util.SparseIntArray;

import java.util.HashMap;

/**
 * Created by pedrocarrillo on 9/21/16.
 */
public class StepManager {

    private int totalPads;
    private SparseIntArray customSteps = new SparseIntArray();

    private static StepManager ourInstance = new StepManager();

    public static StepManager getInstance() {
        return ourInstance;
    }

    private StepManager() {
        totalPads = 3;
    }

    public int getTotalPads() {
        return totalPads;
    }

    public void setTotalPads(int totalPads) {
        this.totalPads = totalPads;
    }

    public void addStep(Integer stepNumber, Integer padSelection) {
        customSteps.put(stepNumber, padSelection);
    }

    public void removeStep(Integer stepNumber) {
        customSteps.delete(stepNumber);
    }

}
