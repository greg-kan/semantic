package kazniisa.kz.reports.DBUtils;

import kazniisa.kz.reports.Main;
import kazniisa.kz.reports.helper;
import org.apache.jena.query.QuerySolution;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUtil {

    public  static int writeTo(List<QuerySolution> querySolutions) {
        int countUpdates = 0;
        try (Connection con = DriverManager.getConnection(helper.url, helper.user, helper.password)) {

            String querySQL = "truncate table public.tb_ambiguous_descriptions";

            try (Statement st = con.createStatement()) {
                st.executeUpdate(querySQL);

            } catch (SQLException ex) {

                Logger lgr = Logger.getLogger(
                        Main.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }

            querySQL = "INSERT INTO public.tb_ambiguous_descriptions(termin_id, termin_label, termin_description, description_count) VALUES(?, ?, ?, ?)";

            try (PreparedStatement pst = con.prepareStatement(querySQL)) {

                con.setAutoCommit(false);

                int count = 0;
                for (QuerySolution querySolution: querySolutions) {
                    pst.setString(1, helper.getURIShortForm(querySolution.getResource("cls").getURI()));
                    pst.setString(2, querySolution.getLiteral("l").getString());
                    pst.setString(3, querySolution.getLiteral("d").getString());
                    pst.setInt(4, querySolution.getLiteral("cntDescr").getInt());
                    count += pst.executeUpdate();
                }

                con.commit();
                countUpdates = count;
            } catch (SQLException ex) {

                try {
                    con.rollback();
                } catch (SQLException ex1) {
                    Logger lgr = Logger.getLogger(
                            Main.class.getName());
                    lgr.log(Level.WARNING, ex1.getMessage(), ex1);
                }

                Logger lgr = Logger.getLogger(
                        Main.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }

        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(
                    Main.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return countUpdates;
    }
}
