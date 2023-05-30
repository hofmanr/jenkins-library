package nl.rhofman.jenkins.utils

import com.cloudbees.groovy.cps.NonCPS

class Validation {
    @NonCPS
    def static paramIsNotNull(String name, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Parameter $name connot be null")
        }
    }

    @NonCPS
    def static paramIsOfType(String name, Object value, Class expectedType) {
        boolean isOfType
        if (expectedType == String.class) {
            isOfType = expectedType.isInstance(value) || GString.class.isInstance(value)
        } else {
            isOfType = expectedType.isInstance(value)
        }
        if (!isOfType) {
            throw new IllegalArgumentException("Parameter $name is no instance of expected type $expectedType")
        }
    }

    @NonCPS
    def static paramIsNotNullAndOfType(String name, Object value, Class expectedClass) {
        paramIsNotNull(name, value) && paramIsOfType(name, value, expectedClass)
    }
}
