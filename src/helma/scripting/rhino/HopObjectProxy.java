package helma.scripting.rhino;

import helma.objectmodel.INode;
import helma.objectmodel.db.Node;
import helma.objectmodel.db.NodeHandle;

import org.mozilla.javascript.Context;

/**
 * Serialization proxy for various flavors of HopObjects/Nodes
 */
class HopObjectProxy implements SerializationProxy {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4808579296683836009L;
	Object ref;
    boolean wrapped = false;

    HopObjectProxy(HopObject obj) {
        INode n = obj.getNode();
        if (n == null) {
            ref = obj.getClassName();
        } else {
            if (n instanceof Node) {
                ref = ((Node) n).getHandle();
            } else {
                ref = n;
            }
        }
        wrapped = true;
    }

    HopObjectProxy(Node node) {
        ref = node.getHandle();
    }

    /**
     * Lookup the actual object in the current scope
     *
     * @return the object represented by this proxy
     */
    public Object getObject(RhinoEngine engine) {
        if (ref instanceof String)
            return engine.core.getPrototype((String) ref);
        else if (ref instanceof NodeHandle) {
            Object n = ((NodeHandle) ref).getNode(engine.app.getWrappedNodeManager());
            return wrapped ? Context.toObject(n, engine.global) : n;
        }
        return Context.toObject(ref, engine.global);
    }

}
