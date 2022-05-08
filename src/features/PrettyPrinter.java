package features;
import java.io.PrintStream;

import static java.lang.String.format;
import static java.lang.System.out;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;
import java.util.*;
public final class PrettyPrinter {
	
	private static final char BORDER_KNOT = '+';
	private static final char HORIZONTAL_BORDER = '-';
	private static final char VERTICAL_BORDER = '|';

	private static final String DEFAULT_AS_NULL = "(NULL)";

	private final PrintStream out;
	private final String asNull;

	public PrettyPrinter(PrintStream out) {
		this(out, DEFAULT_AS_NULL);
	}

	public static void printQuery(ResultSet rs) throws SQLException {
	      ResultSetMetaData rsmd = rs.getMetaData();
	      ArrayList<ArrayList<String>> table = new ArrayList<>();
	      int columnsNumber = rsmd.getColumnCount();
	      table.add(new ArrayList<>());
	      for (int i = 0; i < columnsNumber; i++) {
		 table.get(0).add(rsmd.getColumnName(i+1));
	      }
	      int j = 1;
	      boolean shouldPrint = false;
	      while (rs.next()) {
		 table.add(new ArrayList<>());
		 for (int i = 0; i < columnsNumber; i++) {
		     String cell = rs.getString(i+1);
		     if(cell != null)
		          cell = (cell.length()>30)?cell.substring(0,27)+"...":cell;
		     table.get(j).add(cell);
		 }
		 j++;
		 shouldPrint = true;
	      }
	      PrettyPrinter pp = new PrettyPrinter(System.out);
	      String[][] array = new String[table.size()][];
	      for (int i = 0; i < table.size(); i++) {
		      ArrayList<String> row = table.get(i);
		      array[i] = row.toArray(new String[row.size()]);
	      }
	      if(shouldPrint){
		      pp.print(array);
		      System.out.println();
	      }
	      rs.beforeFirst();
	}

	public PrettyPrinter(PrintStream out, String asNull) {
		if ( out == null ) {
			throw new IllegalArgumentException("No print stream provided");
		}
		if ( asNull == null ) {
			throw new IllegalArgumentException("No NULL-value placeholder provided");
		}
		this.out = out;
		this.asNull = asNull;
	}

	public void print(String[][] table) {
		if ( table == null ) {
			throw new IllegalArgumentException("No tabular data provided");
		}
		if ( table.length == 0 ) {
			return;
		}
		final int[] widths = new int[getMaxColumns(table)];
		adjustColumnWidths(table, widths);
		printPreparedTable(table, widths, getHorizontalBorder(widths));
	}

	private void printPreparedTable(String[][] table, int[] widths, String horizontalBorder) {
		final int lineLength = horizontalBorder.length();
		out.println(horizontalBorder);
		for ( final String[] row : table ) {
			if ( row != null ) {
				out.println(getRow(row, widths, lineLength));
				out.println(horizontalBorder);
			}
		}
	}

	private String getRow(String[] row, int[] widths, int lineLength) {
		final StringBuilder builder = new StringBuilder(lineLength).append(VERTICAL_BORDER);
		final int maxWidths = widths.length;
		for ( int i = 0; i < maxWidths; i++ ) {
			builder.append(padRight(getCellValue(safeGet(row, i, null)), widths[i])).append(VERTICAL_BORDER);
		}
		return builder.toString();
	}

	private String getHorizontalBorder(int[] widths) {
		final StringBuilder builder = new StringBuilder(256);
		builder.append(BORDER_KNOT);
		for ( final int w : widths ) {
			for ( int i = 0; i < w; i++ ) {
				builder.append(HORIZONTAL_BORDER);
			}
			builder.append(BORDER_KNOT);
		}
		return builder.toString();
	}

	private int getMaxColumns(String[][] rows) {
		int max = 0;
		for ( final String[] row : rows ) {
			if ( row != null && row.length > max ) {
				max = row.length;
			}
		}
		return max;
	}

	private void adjustColumnWidths(String[][] rows, int[] widths) {
		for ( final String[] row : rows ) {
			if ( row != null ) {
				for ( int c = 0; c < widths.length; c++ ) {
					final String cv = getCellValue(safeGet(row, c, asNull));
					final int l = cv.length();
					if ( widths[c] < l ) {
						widths[c] = l;
					}
				}
			}
		}
	}

	private static String padRight(String s, int n) {
		return format("%1$-" + n + "s", s);
	}

	private static String safeGet(String[] array, int index, String defaultValue) {
		return index < array.length ? array[index] : defaultValue;
	}

	private String getCellValue(Object value) {
		return value == null ? asNull : value.toString();
	}

}
