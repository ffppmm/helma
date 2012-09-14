package helma.objectmodel;

import java.sql.Timestamp;
import java.util.Date;


final public class NullProperty implements IProperty {
	private static NullProperty instance = new NullProperty();

	private NullProperty() {
		
	}
	
	public static NullProperty getInstance() {
		return instance;
	}
	public String getName() {
		return null;
	}

	public int getType() {
		// TODO this works?
		return (Integer) null;
	}

	public Object getValue() {
		return null;
	}

	public INode getNodeValue() {
		return null;
	}

	public String getStringValue() {
		return null;
	}

	public boolean getBooleanValue() {
		// TODO this works?
		return (Boolean) null;
	}

	public long getIntegerValue() {
		// TODO this works?
		return (Long) null;
	}

	public double getFloatValue() {
		// TODO this works?
		return (Double) null;
	}

	public Date getDateValue() {
		return null;
	}

	public Object getJavaObjectValue() {
		return null;
	}

	public void setStringValue(String value) {
		// Do nothing
	}

	public void setIntegerValue(long value) {
		// Do nothing
	}

	public void setFloatValue(double value) {
		// Do nothing
	}

	public void setBooleanValue(boolean value) {
		// Do nothing
	}

	public void setDateValue(Date value) {
		// Do nothing
	}

	public void setJavaObjectValue(Object value) {
		// Do nothing
	}

	public void setNodeValue(INode value) {
		// Do nothing
	}

	public Timestamp getTimestampValue() {
		return null;
	}

}
