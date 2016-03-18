package activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Data {
	public static List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
	public static Map<String, List<Map<String, Object>>> childs = new HashMap<String, List<Map<String, Object>>>();

	public List<Map<String, Object>> initGroups() {
		groups.clear();
		Map<String, Object> group = new HashMap<String, Object>();
		group.put("name", "男装");
		group.put("id", 1);
		//group.put("iv", R.drawable.liangyou);
		groups.add(group);
		group = new HashMap<String, Object>();
		group.put("name", "女装");
		group.put("id", 2);
		//group.put("iv", R.drawable.jiushui);
		groups.add(group);
		return groups;
	}

	public Map<String, List<Map<String, Object>>> initChilds() {
		childs.clear();
		List<Map<String, Object>> child = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", 11);
		map.put("name", "大米");
		child.add(map);
		
		map = new HashMap<String, Object>();
		map.put("id", 12);
		map.put("name", "卫衣");
		child.add(map);
		
		map = new HashMap<String, Object>();
		map.put("id", 13);
		map.put("name", "衬衫");
		child.add(map);
		
		map = new HashMap<String, Object>();
		map.put("id", 14);
		map.put("name", "夹克");
		child.add(map);
		
		map = new HashMap<String, Object>();
		map.put("id", 15);
		map.put("name", "牛仔裤");
		child.add(map);
		
		map = new HashMap<String, Object>();
		map.put("id", 16);
		map.put("name", "风衣");
		child.add(map);
		
		map = new HashMap<String, Object>();
		map.put("id", 17);
		map.put("name", "毛衣");
		child.add(map);
		
		map = new HashMap<String, Object>();
		map.put("id", 18);
		map.put("name", "短袖");
		child.add(map);
		
		/*map = new HashMap<String, Object>();
		map.put("id", 19);
		map.put("name", "玉米油");
		child.add(map);
		
		map = new HashMap<String, Object>();
		map.put("id", 20);
		map.put("name", "花生油");
		child.add(map);
		
		map = new HashMap<String, Object>();
		map.put("id", 21);
		map.put("name", "大豆油");
		child.add(map);
		
		map = new HashMap<String, Object>();
		map.put("id", 22);
		map.put("name", "芝麻油");
		child.add(map);*/
		childs.put("1", child);
///////////////////////////////////////////////////////////////
		child = new ArrayList<Map<String, Object>>();
		map = new HashMap<String, Object>();
		map.put("id", 23);
		map.put("name", "连衣裙");
		child.add(map);
		
		map = new HashMap<String, Object>();
		map.put("id", 24);
		map.put("name", "紧身裤");
		child.add(map);
		
		childs.put("2", child);
		return childs;
	}

}
