package com.example.david.lists.view.addedit.userlist.buildlogic;

import android.app.Application;

import androidx.fragment.app.Fragment;

import com.example.david.lists.common.buildlogic.ViewCommonModule;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.view.addedit.common.buildlogic.AddEditDialogCommonModule;
import com.example.david.lists.view.addedit.userlist.AddEditUserListDialog;

import javax.inject.Named;

import dagger.BindsInstance;
import dagger.Component;

import static com.example.david.lists.view.addedit.common.buildlogic.AddEditNamedConstants.ID;
import static com.example.david.lists.view.addedit.common.buildlogic.AddEditNamedConstants.TITLE;

@ViewScope
@Component(modules = {
        AddEditUserListDialogModule.class,
        AddEditDialogCommonModule.class,
        ViewCommonModule.class
})
public interface AddEditUserListDialogComponent {
    void inject(AddEditUserListDialog fragment);

    @Component.Builder
    interface Builder {
        AddEditUserListDialogComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder fragment(Fragment fragment);

        @BindsInstance
        Builder id(@Named(ID) String id);

        @BindsInstance
        Builder title(@Named(TITLE) String title);
    }
}