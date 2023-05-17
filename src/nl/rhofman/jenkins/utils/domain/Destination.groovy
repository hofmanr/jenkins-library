package nl.rhofman.jenkins.utils.domain

class Destination implements Serializable {
    String name
    String stage

    @Override
    String toString() {
        return "Destination[name: $name, stage: $stage]"
    }
}
