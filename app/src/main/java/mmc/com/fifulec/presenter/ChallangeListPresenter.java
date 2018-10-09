package mmc.com.fifulec.presenter;

import java.util.List;

import javax.inject.Inject;

import mmc.com.fifulec.AppContext;
import mmc.com.fifulec.ChallangeListContract;
import mmc.com.fifulec.di.AppScope;
import mmc.com.fifulec.model.Challange;
import mmc.com.fifulec.model.OnUserClickedListener;
import mmc.com.fifulec.model.User;
import mmc.com.fifulec.service.CallBack;
import mmc.com.fifulec.service.ChallangeService;

@AppScope
public class ChallangeListPresenter {

    private ChallangeListContract.View view;

    private final ChallangeService challangeService;
    private AppContext appContext;

    @Inject
    public ChallangeListPresenter(ChallangeService challangeService, AppContext appContext) {
        this.challangeService = challangeService;
        this.appContext = appContext;
    }

    public void onCreate(final ChallangeListContract.View view) {
        this.view = view;

        updateChallangesList();
    }

    private void updateChallangesList() {
        challangeService.getChallangesFromUser(appContext.getUser(), new CallBack<List<Challange>>() {
            @Override
            public void response(List<Challange> challanges) {
                view.setChalanges4Adapter(challanges);
            }

            @Override
            public void error() {

            }
        });
    }

    public void onAddChallangeClicked() {
        appContext.setOnUserClickedListener(new OnUserClickedListener() {
            @Override
            public void onUserSelect(User user) {
                challangeService.createChallange(appContext.getUser(), user);
                updateChallangesList();
            }
        });

        view.showUsersList();
    }
}
