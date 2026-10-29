package edu.sjsu.fwjs;

import java.util.Map;
import java.util.HashMap;

public class Environment {
    private Map<String,Value> env = new HashMap<String,Value>();
    private Environment outerEnv;

    /**
     * Constructor for global environment
     */
    public Environment() {}

    /**
     * Constructor for local environment of a function
     */
    public Environment(Environment outerEnv) {
        this.outerEnv = outerEnv;
    }

    /**
     * Handles the logic of resolving a variable.
     * If the variable name is in the current scope, it is returned.
     * Otherwise, search for the variable in the outer scope.
     * If we are at the outermost scope (AKA the global scope)
     * null is returned (similar to how JS returns undefined.
     */
    public Value resolveVar(String varName) {
        // YOUR CODE HERE - check if the variable exists in the current environment, if not, check the outer environment
        if (env.containsKey(varName)) {
            return env.get(varName);
        } else if (outerEnv != null) {
            return outerEnv.resolveVar(varName);
        } 
        return null;
    }

    /**
     * Used for updating existing variables.
     * If a variable has not been defined previously in the current scope,
     * or any of the function's outer scopes, the var is stored in the global scope.
     */
    public void updateVar(String key, Value v) {
        // YOUR CODE HERE - check if the variable exists in the current environment (map), if not, check the outer environment, if not found, update in the global environment
        if (env.containsKey(key)) {
            env.put(key, v);
        } else if (outerEnv != null) {
            // Recursively update the variable in the outer environment
            outerEnv.updateVar(key, v);
        } else {
            env.put(key, v);
        }
    }

    /**
     * Creates a new variable in the local scope.
     * If the variable has been defined in the current scope previously,
     * a RuntimeException is thrown.
     */
    public void createVar(String key, Value v) {
        // YOUR CODE HERE - check if the variable exists in the current environment, if it does, throw a RuntimeException, otherwise, create the variable in the current environment
        if (env.containsKey(key)) {
            // Variable already exists in the current environment
            throw new RuntimeException("Variable " + key + " already defined in the current scope.");
        } else {
            // Create the variable and store its value in the current environment (it's okay if it shadows a variable in an outer scope)
            env.put(key, v);
        }
    }
}
