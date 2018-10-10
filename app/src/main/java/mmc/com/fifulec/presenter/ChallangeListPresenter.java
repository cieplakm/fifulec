package mmc.com.fifulec.presenter;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import mmc.com.fifulec.service.ChallangeService;
import mmc.com.fifulec.utils.AppContext;
import mmc.com.fifulec.contract.ChallangeListContract;
import mmc.com.fifulec.di.AppScope;
import mmc.com.fifulec.model.Challange;
import mmc.com.fifulec.model.OnUserClickedListener;
import mmc.com.fifulec.model.User;


@AppScope
public class ChallangeListPresenter {

    private ChallangeListContract.View view;

    private final ChallangeService challengeService;
    private AppContext appContext;

    @Inject
    public ChallangeListPresenter(ChallangeService challengeService, AppContext appContext) {
        this.challengeService = challengeService;
        this.appContext = appContext;
    }

    public void onCreate(final ChallangeListContract.View view) {
        this.view = view;

        updateChallangesList();
    }

    private void updateChallangesList() {

        challengeService.observable(appContext.getUser())
                .subscribe(new Consumer<Challange>() {
                    @Override
                    public void accept(Challange challange) throws Exception {
                        view.setChalanges4Adapter(Arrays.asList(challange));
                    }
                });



//        challengeService.getChallangesFromUser(appContext.getUser(), new CallBack<List<Challange>>() {
//            @Override
//            public void response(List<Challange> challanges) {
//                view.setChalanges4Adapter(challanges);
//            }
//
//            @Override
//            public void error() {
//
//            }
//        });
    }

    public void onAddChallangeClicked() {
        appContext.setOnUserClickedListener(new OnUserClickedListener() {
            @Override
            public void onUserSelect(User user) {
                challengeService.createChallange(appContext.getUser(), user);
                updateChallangesList();
            }
        });

        view.showUsersList();
    }
}
