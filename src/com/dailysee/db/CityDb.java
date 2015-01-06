package com.dailysee.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.provider.BaseColumns;

import com.dailysee.bean.CityEntity;

public class CityDb extends BaseDb {
	
	public static class Table implements BaseColumns {

		public static final String TABLE_NAME = "tb_city";

		public static final String CITY_ID = "city_id";
		public static final String NAME = "name";
		
		public static final String PARENT_ID = "parent_id";
		public static final String LEVEL_TYPE = "level_type"; 

		public static final String DEFAULT_SORT_ORDER = Table._ID + " DESC";

		public static final String[] PROJECTION = { _ID, CITY_ID, NAME, PARENT_ID, LEVEL_TYPE };
	}
	
	public CityDb(Context context) {
		super(context);
	}

	@Override
	public String getTableName() {
		return Table.TABLE_NAME;
	}

	protected static String getCreateTableSQL() {
		
		StringBuilder sb = new StringBuilder();
		sb.append(CREATE_TABLE_PREFIX).append(Table.TABLE_NAME).append(BRACKET_LEFT);
		sb.append(Table._ID).append(COLUMN_TYPE.INTEGER).append(PRIMARY_KEY).append(COMMA);
		sb.append(Table.CITY_ID).append(COLUMN_TYPE.INTEGER).append(COMMA);
		sb.append(Table.NAME).append(COLUMN_TYPE.TEXT).append(COMMA);
		sb.append(Table.PARENT_ID).append(COLUMN_TYPE.INTEGER).append(COMMA);
		sb.append(Table.LEVEL_TYPE).append(COLUMN_TYPE.INTEGER);
		sb.append(BRACKET_RIGHT);

		return sb.toString();
	}

	protected static String getDropTableSQL() {
		return DROP_TABLE_PREFIX + Table.TABLE_NAME;
	}

	@Override
	protected Object parseCursor(Cursor cursor) {
		CityEntity entity = new CityEntity();
		
		entity.cityId = cursor.getInt(cursor.getColumnIndexOrThrow(Table.CITY_ID));
		entity.name = cursor.getString(cursor.getColumnIndexOrThrow(Table.NAME));
		entity.parentId = cursor.getInt(cursor.getColumnIndexOrThrow(Table.PARENT_ID));
		entity.levelType = cursor.getInt(cursor.getColumnIndexOrThrow(Table.LEVEL_TYPE));
		
		return entity;
	}

	public List<CityEntity> findAll() {
		List<CityEntity> list = new ArrayList<CityEntity>();
		
        String selection = null;
        String[] selectionArgs = null;
        
        Cursor cursor = null;
        try {
        	checkDb();
            cursor = db.query(Table.TABLE_NAME, Table.PROJECTION, selection, selectionArgs, null, null, Table.CITY_ID + " asc" );
            while (cursor != null && cursor.moveToNext()) {
            	CityEntity CityEntity = (CityEntity)parseCursor(cursor);
            	list.add(CityEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
	}
	
	public List<CityEntity> findCityRegionInfo(int parentId) {
		List<CityEntity> list = new ArrayList<CityEntity>();

		String selection = String.format(" %s = ? ", Table.PARENT_ID);
		String[] selectionArgs = new String[] { Integer.toString(parentId) };
        
        Cursor cursor = null;
        try {
        	checkDb();
            cursor = db.query(Table.TABLE_NAME, Table.PROJECTION, selection, selectionArgs, null, null, Table.CITY_ID + " asc" );
            while (cursor != null && cursor.moveToNext()) {
            	CityEntity CityEntity = (CityEntity)parseCursor(cursor);
            	list.add(CityEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
	}
	
	public void saveAll(List<CityEntity> list) {
		checkDb();
		beginTransaction();
		try {
			clearAllData();
			if (list != null && list.size() > 0) {
				for (CityEntity entity : list) {
					insert(entity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			endTransaction();
		}
	}
	
	public void saveCityInfo(int provinceId, List<CityEntity> list) {
		checkDb();
		beginTransaction();
		try {
			delete(provinceId);
			if (list != null && list.size() > 0) {
				for (CityEntity entity : list) {
					insert(entity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			endTransaction();
		}
	}
	
	public void saveCityRegionInfo(int cityId, List<CityEntity> list) {
		checkDb();
		beginTransaction();
		try {
			delete(cityId);
			if (list != null && list.size() > 0) {
				for (CityEntity entity : list) {
					insert(entity);
					if (entity.cityChilds != null && entity.cityChilds.size() > 0) {
						for (CityEntity child : entity.cityChilds) {
							insert(child);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			endTransaction();
		}
	}

	public int getCount(int parentId) {
		int count = 0;
		Cursor cursor = null;
		try {
			checkDb();

			String selection = String.format(" %s = ? ", Table.PARENT_ID);
			String[] selectionArgs = new String[] { Integer.toString(parentId) };

			cursor = db.query(Table.TABLE_NAME, Table.PROJECTION, selection, selectionArgs, null, null, Table._ID + " asc limit 1");
			if (cursor != null) {
				cursor.moveToFirst();
				count = cursor.getCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return count;
	}

	public void insert(CityEntity entity) {
		if (entity != null) {
			checkDb();
			
			ContentValues values = new ContentValues();
			values.put(Table.CITY_ID, entity.cityId);
			values.put(Table.NAME, entity.name);
			values.put(Table.PARENT_ID, entity.parentId);
			values.put(Table.LEVEL_TYPE, entity.levelType);
			
			db.insert(Table.TABLE_NAME, null, values);
		}
	}
	
	public void update(CityEntity entity) {
		if (entity != null) {
			checkDb();
			
			String whereClause = String.format(" %s = ? and %s = ? ", Table.CITY_ID, Table.PARENT_ID);
			String[] whereArgs = new String[] { Integer.toString(entity.cityId), Integer.toString(entity.parentId) };
			
			ContentValues values = new ContentValues();
			values.put(Table.CITY_ID, entity.cityId);
			values.put(Table.NAME, entity.name);
			values.put(Table.PARENT_ID, entity.parentId);
			values.put(Table.LEVEL_TYPE, entity.levelType);
			
			db.update(Table.TABLE_NAME, values, whereClause, whereArgs);
		}
	}

	public void delete(int cityId) {
		try {
			checkDb();
			String whereClause = String.format(" %s = ? ", Table.PARENT_ID);
			String[] whereArgs = new String[] { Integer.toString(cityId) };
			db.delete(Table.TABLE_NAME, whereClause, whereArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeDbAndCursor();
		}
	}

}
