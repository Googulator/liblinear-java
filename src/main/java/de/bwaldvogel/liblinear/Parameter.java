package de.bwaldvogel.liblinear;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Random;


public final class Parameter implements Cloneable {

    private static final long   DEFAULT_RANDOM_SEED = 0L;

    double C;

    /** stopping tolerance */
    double eps;

    int max_iters = 1000; // maximal iterations
    
    int dual_solver_max_iters = 300;

    SolverType solverType;

    double[] weight = null;

    int[] weightLabel = null;

    double p = 0.1;

    double nu = 0.5;

    /**
     * Initial-solution specification (only supported for {@link SolverType#L2R_LR} and {@link SolverType#L2R_L2LOSS_SVC})
     */
    double[] init_sol = null;

    boolean regularize_bias = true;

    Random random = new Random(DEFAULT_RANDOM_SEED);

    public Parameter(SolverType solver, double C, double eps) {
        setSolverType(solver);
        setC(C);
        setEps(eps);
    }

    public Parameter(SolverType solver, double C, int max_iters, double eps) {
        setSolverType(solver);
        setC(C);
        setEps(eps);
        setMaxIters(max_iters);
    }

    public Parameter(SolverType solver, double C, int max_iters, int dual_solver_max_iters, double eps) {
        setSolverType(solver);
        setC(C);
        setEps(eps);
        setMaxIters(max_iters);
        setDualSolverMaxIters(dual_solver_max_iters);
    }

    public Parameter(SolverType solverType, double C, double eps, double p) {
        setSolverType(solverType);
        setC(C);
        setEps(eps);
        setP(p);
    }

    public Parameter(SolverType solverType, double C, double eps, int max_iters, double p) {
        setSolverType(solverType);
        setC(C);
        setEps(eps);
        setMaxIters(max_iters);
        setP(p);
    }

    public Parameter(SolverType solverType, double C, double eps, int max_iters, int dual_solver_max_iters, double p) {
        setSolverType(solverType);
        setC(C);
        setEps(eps);
        setMaxIters(max_iters);
        setDualSolverMaxIters(dual_solver_max_iters);
        setP(p);
    }

    /**
     * <p>nr_weight, weight_label, and weight are used to change the penalty
     * for some classes (If the weight for a class is not changed, it is
     * set to 1). This is useful for training classifier using unbalanced
     * input data or with asymmetric misclassification cost.</p>
     *
     * <p>Each weight[i] corresponds to weight_label[i], meaning that
     * the penalty of class weight_label[i] is scaled by a factor of weight[i].</p>
     *
     * <p>If you do not want to change penalty for any of the classes,
     * just set nr_weight to 0.</p>
     */
    public void setWeights(double[] weights, int[] weightLabels) {
        if (weights == null)
            throw new IllegalArgumentException("'weight' must not be null");
        if (weightLabels == null || weightLabels.length != weights.length)
            throw new IllegalArgumentException("'weightLabels' must have same length as 'weight'");
        this.weightLabel = Arrays.copyOf(weightLabels, weightLabels.length);
        this.weight = Arrays.copyOf(weights, weights.length);
    }

    /**
     * @see #setWeights(double[], int[])
     */
    public double[] getWeights() {
        if (weight == null) {
            return null;
        }
        return Arrays.copyOf(weight, weight.length);
    }

    /**
     * @see #setWeights(double[], int[])
     */
    public int[] getWeightLabels() {
        if (weightLabel == null) {
            return null;
        }
        return Arrays.copyOf(weightLabel, weightLabel.length);
    }

    /**
     * the number of weights
     * @see #setWeights(double[], int[])
     */
    public int getNumWeights() {
        if (weight == null)
            return 0;
        return weight.length;
    }

    /**
     * C is the cost of constraints violation. (we usually use 1 to 1000)
     */
    public void setC(double C) {
        if (C <= 0)
            throw new IllegalArgumentException("C must not be <= 0");
        this.C = C;
    }

    public double getC() {
        return C;
    }

    /**
     * eps is the stopping criterion. (we usually use 0.01).
     */
    public void setEps(double eps) {
        if (eps <= 0)
            throw new IllegalArgumentException("eps must not be <= 0");
        this.eps = eps;
    }

    public double getEps() {
        return eps;
    }

    public void setMaxIters(int iters) {
        if (iters <= 0)
            throw new IllegalArgumentException("max iters not be <= 0");
        this.max_iters = iters;
    }

    public int getMaxIters() {
        return max_iters;
    }

    public void setDualSolverMaxIters(int iters) {
        if (iters <= 0)
            throw new IllegalArgumentException("dual solver max iters not be <= 0");
        this.dual_solver_max_iters = iters;
    }

    public int getDualSolverMaxIters() {
        return dual_solver_max_iters;
    }

    public void setSolverType(SolverType solverType) {
        if (solverType == null)
            throw new IllegalArgumentException("solver type must not be null");
        this.solverType = solverType;
    }

    public SolverType getSolverType() {
        return solverType;
    }

    /**
     * set the epsilon in loss function of epsilon-SVR (default 0.1)
     */
    public void setP(double p) {
        if (p < 0)
            throw new IllegalArgumentException("p must not be less than 0");
        this.p = p;
    }

    public double getP() {
        return p;
    }

    /*
     * Sets the initial-solution specification.
     * Only supported for {@link SolverType#L2R_LR} and {@link SolverType#L2R_L2LOSS_SVC}.
     */
    public void setInitSol(double[] init_sol) {
        if (init_sol == null)
            this.init_sol = null;
        else {
            this.init_sol = Arrays.copyOf(init_sol, init_sol.length);
        }
    }

    public double[] getInitSol() {
        if (init_sol == null) {
            return null;
        }
        return Arrays.copyOf(init_sol, init_sol.length);
    }

    public void setNu(double nu) {
        if (nu <= 0)
            throw new IllegalArgumentException("nu must not be <=0");
        if (nu >= 1)
            throw new IllegalArgumentException("nu must not be >=1");
        this.nu = nu;
    }

    public double getNu() {
        return nu;
    }

    public void setRegularizeBias(boolean regularizeBias) {
        this.regularize_bias = regularizeBias;
    }

    public boolean isRegularizeBias() {
        return regularize_bias;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    @Override
    public Parameter clone() {
        Parameter clone = new Parameter(solverType, C, eps, max_iters, dual_solver_max_iters, p);
        clone.weight = weight == null ? null : weight.clone();
        clone.weightLabel = weightLabel == null ? null : weightLabel.clone();
        clone.init_sol = init_sol;
        clone.p = p;
        clone.nu = nu;
        clone.regularize_bias = regularize_bias;
        clone.random = deepClone(random);
        return clone;
    }

    private static Random deepClone(Random random) {
        try {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream out = new ObjectOutputStream(baos);) {
                out.writeObject(random);
                try (ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
                     ObjectInputStream in = new ObjectInputStream(bis)) {
                    return (Random)in.readObject();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to clone " + random, e);
        }
    }

}
