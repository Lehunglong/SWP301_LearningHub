package swp391.group2.learninghub.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.group2.learninghub.DAO.FeatureDAO;
import swp391.group2.learninghub.Model.Feature;

import java.util.List;

@Service
public class FeatureServieImpl implements  FeatureService{
    @Autowired
    private final FeatureDAO featureDAO;
    public FeatureServieImpl(FeatureDAO featureDAO) {
        this.featureDAO = featureDAO;
    }
    @Override
    public List<Feature> allFeatures() {
        return null;
    }
}