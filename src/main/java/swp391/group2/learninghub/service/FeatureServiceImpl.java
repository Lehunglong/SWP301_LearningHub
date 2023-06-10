package swp391.group2.learninghub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp391.group2.learninghub.dao.FeatureDAO;
import swp391.group2.learninghub.model.Feature;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class FeatureServiceImpl implements FeatureService{
    @Autowired
    private final FeatureDAO featureDAO;

    @Autowired
    public FeatureServiceImpl(FeatureDAO featureDAO) {
        this.featureDAO = featureDAO;
    }

    @Override
    public List<Feature> showAll() {
        return featureDAO.findAll();
    }

    @Override
    public void setActive(int id,String mess) throws Exception {
        Optional<Feature> optionalFeature =featureDAO.findById(id);
        Logger logger = Logger.getLogger(FeatureServiceImpl.class.getName());
        if(optionalFeature.isPresent()){
            Feature feature = optionalFeature.get();
            feature.setDescription(mess);
            logger.log(Level.FINE,"retrieve data {0}",feature);
            if(feature.getName() == null){
                throw new Exception("item is null");
            }else{
                feature.setActive(!feature.isActive());
                featureDAO.save(feature);
            }
        }else{
            throw new RuntimeException("feature not found");
        }

    }

    @Override
    public Feature findFeatureById(int id) {
        Optional<Feature> optionalFeature = featureDAO.findById(id);
        return optionalFeature.orElse(null);
    }
}
