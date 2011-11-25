package rain;

public interface DynamicIPAddressDAO {
	
	public void saveOrUpdate(DynamicIpAddress ip);
	
	public void delete(DynamicIpAddress ip);
	
	public DynamicIpAddress findByName(String name);
	
	public DynamicIpAddress findByCurrentValue(String ipValue);
	
	

}
