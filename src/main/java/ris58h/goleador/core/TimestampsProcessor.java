package ris58h.goleador.core;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static ris58h.goleador.core.Utils.readInputToString;

public class TimestampsProcessor {
    public static void process(String dirName, String inSuffix, String outFileName) throws Exception {
        Path dirPath = Paths.get(dirName);
        String inPostfix = inSuffix + ".txt";
        String inGlob = "*" + inPostfix;
        SortedMap<Integer, Score> scores = new TreeMap<>();
        System.out.println("Loadings scores");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, inGlob)) {
            for (Path path : stream) {
                String scoreString;
                try (InputStream is = new BufferedInputStream(new FileInputStream(path.toFile()))) {
                    scoreString = readInputToString(is);
                }
                Score score = Score.parseScore(scoreString);
                String name = path.getName(path.getNameCount() - 1).toString();
                String prefix = name.substring(0, name.length() - inPostfix.length());
                int frameNumber = Integer.parseInt(prefix);
                scores.put(frameNumber, score);
            }
        }

        List<String> timestamps;
        if (scores.isEmpty()) {
            System.out.println("No scores found!");
            timestamps = Collections.emptyList();
        } else {
            System.out.println("Finding timestamps");
            List<Integer> changes = scoreChangedFrames(scores);
            List<String> changeLines = changes.stream().map(Object::toString).collect(Collectors.toList());
            Files.write(dirPath.resolve("score-frames.txt"), changeLines, Charset.forName("UTF-8"));
            timestamps = changes.stream()
                    .map(frame -> frame - 1) // The first frame is a thumbnail.
                    .map(frame -> frame - 1) //TODO: Is the second one a thumbnail too?!
                    .map(frame -> Integer.max(0, frame - 15)) // We interested in time that is seconds before score changed.
                    .map(Utils::timestamp)
                    .collect(Collectors.toList());
        }
        Files.write(dirPath.resolve(outFileName), timestamps, Charset.forName("UTF-8"));
    }

    static List<Integer> scoreChangedFrames(SortedMap<Integer, Score> scores) {
        List<Integer> goals = new ArrayList<>();
        Score prevScore = Score.of(0, 0); // Score should start with 0-0.
        for (Map.Entry<Integer, Score> entry : scores.entrySet()) {
            Integer frame = entry.getKey();
            Score score = entry.getValue();
            boolean leftSame = score.left == prevScore.left;
            boolean leftChanged = score.left == prevScore.left + 1;
            boolean rightSame = score.right == prevScore.right;
            boolean rightChanged = score.right == prevScore.right + 1;
            if ((leftSame || leftChanged) && (rightSame || rightChanged)) {
                if ((leftChanged && rightSame) || (leftSame && rightChanged)) {
                    goals.add(frame);
                }
                prevScore = score;
            }
        }
        return goals;
    }
}