package architecture.domain;

import java.util.ArrayList;
import java.util.List;

import architecture.data.local.entity.People;
import architecture.data.model.people.ApiPeopleResult;

public class PeopleConversionHelper {

    public List<People> convertApiDataToLocalData(List<ApiPeopleResult> apiResult) {
        List<People> listEntity = new ArrayList<>();
        for(ApiPeopleResult result : apiResult) {
            People people = result.toPeople();
            listEntity.add(people);
        }
        return listEntity;
    }

}
