package helma.scripting.rhino;

/**
 * Serialization proxy for global scope
 */
class GlobalProxy implements SerializationProxy {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3200125667487274257L;
	boolean shared;

    GlobalProxy(GlobalObject scope) {
        shared = !scope.isThreadScope;
    }

    /**
     * Lookup the actual object in the current scope
     *
     * @return the object represented by this proxy
     */
    public Object getObject(RhinoEngine engine) {
        return shared ? engine.core.global : engine.global;
    }
}
