package util.jon;

import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Input-reader class.
 * Not quite as elegant as I had hoped, but it does the job.
 */
public class InputReader implements Closeable {

    private BufferedReader bufferedReader;

    public InputReader(InputStream stream) {
        bufferedReader = new BufferedReader(new InputStreamReader(stream));
    }

    /**
     * Looks through the given input stream for a line matching the patten.
     * Consumes the stream as it goes.
     * @param pattern The pattern to look for.
     * @return The groupings found. Adheres to normal regex convention,
     *         meaning that group: 0 is the entire string and then the capture groups.
     */
    public List<String> findNext(Pattern pattern) {
        try {
            String line = bufferedReader.readLine();
            Matcher matcher = pattern.matcher(line);
            while (!matcher.matches()) {
                line = bufferedReader.readLine();
                if (line == null) {
                    matcher = null;
                    break;
                }
                matcher = pattern.matcher(line);
            }
            LinkedList<String> groups = new LinkedList<>();
            if (matcher != null) {
                for (int i = 0; i < matcher.groupCount() + 1; i++) {
                    groups.addFirst(matcher.group(i));
                }
            }
            Collections.reverse(groups);
            return groups;
        } catch (IOException e) {
            try {
                close();
            } catch (IOException closeException) {
                System.err.println("Can't close an already closed closeable.");
                                  //CLose? cLosE? Close close close? Closed.
            }
            return new LinkedList<>();
        }
    }

    public List<String> findNext(String pattern) {
        return findNext(Pattern.compile(pattern));
    }

    /**
     * Determines whether there is anything left in the stream.
     * @return whether another line is present.
     */
    public boolean ready() {
        try {
            return bufferedReader.ready();
        } catch (IOException e) {
            return true;
        }
    }

    public void close() throws IOException {
        bufferedReader.close();
    }
}
