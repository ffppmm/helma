package helma.scripting.rhino;

/**
 * Serialization proxy for app, req, res, path objects.
 */
class ScriptBeanProxy implements SerializationProxy {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1002489933060844917L;
	String name;

    ScriptBeanProxy(String name) {
        this.name = name;
    }

    /**
     * Lookup the actual object in the current scope
     *
     * @return the object represented by this proxy
     */
    public Object getObject(RhinoEngine engine) {
        return engine.global.get(name, engine.global);
    }

}